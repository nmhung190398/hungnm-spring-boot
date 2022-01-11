package net.devnguyen.common.redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.devnguyen.common.redis.config.RedisCacheConfig;
import net.devnguyen.common.redis.config.RedisCacheProps;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RedisCacheManagerFactory implements CacheManagerFactory {
    private final RedisConnectionFactory redisConnectionFactory;
    private final RedisCacheConfig redisCacheConfig;

    public RedisCacheManagerFactory(RedisConnectionFactory redisConnectionFactory, RedisCacheConfig redisCacheConfig) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.redisCacheConfig = redisCacheConfig;
    }

    @Override
    public CacheManager createCacheManager() {
        Assert.notNull(redisCacheConfig, "Could not create RedisCacheManager using null RedisCacheConfig");
        RedisCacheProps defaultProps = redisCacheConfig.getRedis();
        RedisCacheConfiguration defaultConfig = createConfiguration(defaultProps, null);
        Map<String, RedisCacheProps> cacheProps = defaultProps.getCacheProps();
        Set<Map.Entry<String, RedisCacheConfiguration>> cacheConfigs = cacheProps == null
                ? new HashSet<>()
                : cacheProps.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), createConfiguration(entry.getValue(), defaultProps)))
                .collect(Collectors.toSet());
        return createRedisCacheManager(defaultConfig, cacheConfigs);
    }

    protected RedisSerializer<Object> createSerializer(RedisCacheProps props, RedisCacheProps defaultProps) {
        Assert.state(
                props != null || defaultProps != null,
                "Could not create redis serializer with both  null props");
        if (props == null) {
            props = defaultProps;
        } else if (props.getValueFormat() == null) {
            props.setValueFormat(defaultProps.getValueFormat());
        }

        switch (props.getValueFormat()) {
            case RedisCacheProps.JSON_VALUE_FORMAT:
                return new GenericJackson2JsonRedisSerializer(createObjectMapper());
            default:
                return RedisSerializer.java();
        }
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

    private RedisCacheConfiguration createConfiguration(RedisCacheProps props, RedisCacheProps defaultProps) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(props.getTimeToLive() == null ? defaultProps.getTimeToLive() : props.getTimeToLive())
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        createSerializer(props, defaultProps)));
    }

    private RedisCacheManager createRedisCacheManager(
            RedisCacheConfiguration rootConfig, Set<Map.Entry<String, RedisCacheConfiguration>> cacheConfigs) {
        RedisCacheManager.RedisCacheManagerBuilder redisCacheManagerBuilder =
                RedisCacheManager.builder(redisConnectionFactory)
                        .cacheDefaults(rootConfig);
        cacheConfigs.forEach(entry -> redisCacheManagerBuilder.withCacheConfiguration(entry.getKey(), entry.getValue()));
        return redisCacheManagerBuilder.build();
    }
}
