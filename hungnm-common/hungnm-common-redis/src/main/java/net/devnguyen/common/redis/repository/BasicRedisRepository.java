package net.devnguyen.common.redis.repository;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BasicRedisRepository<V> implements IBasicRedisRepository<V>{
    private final RedisTemplate<String, V> redisTemplate;
    private final ValueOperations<String,V> valueOperations;
    private final String prefixKey;
    private final Function<V,String> fnGetId;
    private final SetOperations<String,String> setOperations;

    public BasicRedisRepository(RedisConnectionFactory redisConnectionFactory, RedisTemplate<String,String> stringRedisTemplate , String prefixKey, Function<V, String> fnGetId) {
        this.redisTemplate = initRedisTemplate(redisConnectionFactory);
        this.prefixKey = prefixKey;
        this.fnGetId = fnGetId;
        this.redisTemplate.afterPropertiesSet();
        this.valueOperations = redisTemplate.opsForValue();
        this.setOperations = stringRedisTemplate.opsForSet();
    }

    private RedisTemplate<String, V> initRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        var redisTemplate = new RedisTemplate<String, V>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer(createObjectMapper()));
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(createObjectMapper()));
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(createObjectMapper()));
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }


    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        GenericJackson2JsonRedisSerializer.registerNullValueSerializer(objectMapper, null);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

    private String buildRedisKey(String id){
        if(!StringUtils.hasLength(id)){
            throw new NullPointerException("id not null");
        }
        return prefixKey + ":" + id;
    }

    private String buildRedisKey(V v){
        return buildRedisKey(fnGetId.apply(v));
    }

    @Override
    public List<V> findAllById(Collection<String> collection) {
        return valueOperations.multiGet(collection.stream().map(this::buildRedisKey).collect(Collectors.toSet()))
                .stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public void save(V o) {
        var key = buildRedisKey(o);
        var id = this.fnGetId.apply(o);
        valueOperations.set(key,o);
        this.setOperations.add(this.prefixKey,id);
    }

    @Override
    public void saveAll(List<V> list) {
        var map = list.stream().collect(Collectors.toMap(this::buildRedisKey,r -> r));
        var ids = list.stream().map(this.fnGetId).toArray(String[]::new);
        this.setOperations.add(this.prefixKey,ids);
        valueOperations.multiSet(map);
    }

    @Override
    public void deletedById(String id) {
        var key = buildRedisKey(id);
        redisTemplate.delete(key);

        this.setOperations.remove(this.prefixKey,id);
    }

    @Override
    public Optional<V> findById(String id) {
        return Optional.ofNullable(valueOperations.get(buildRedisKey(id)));
    }

    @Override
    public List<V> findAll() {
        var ids = this.setOperations.members(this.prefixKey);
        if(CollectionUtils.isEmpty(ids)){
            return new ArrayList<>();
        }
        return findAllById(ids.stream().map(this::buildRedisKey).collect(Collectors.toSet()));
    }

    @Override
    public long count() {
        var ids = this.setOperations.members(this.prefixKey);
        if(CollectionUtils.isEmpty(ids)){
            return 0L;
        }
        return ids.size();
    }

    @Override
    public boolean existsById(String id) {
        return Optional.ofNullable(this.setOperations.isMember(this.prefixKey,id)).orElse(false);
    }

    @Override
    public void deleteAll() {
        var ids = this.setOperations.members(this.prefixKey);
        if(CollectionUtils.isEmpty(ids)){
            return;
        }
        this.redisTemplate.delete(ids.stream().map(this::buildRedisKey).collect(Collectors.toSet()));
        this.setOperations.remove(this.prefixKey,ids);
    }
}
