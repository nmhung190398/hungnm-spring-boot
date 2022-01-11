package net.devnguyen.bestpractices.redis.findallbyids.repository;

import net.devnguyen.bestpractices.redis.findallbyids.domain.HighSpeedDomain;
import net.devnguyen.common.redis.repository.BasicRedisRepository;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AutoConfigureAfter(value = RedisConnectionFactory.class)
public class HighSpeedRepository extends BasicRedisRepository<HighSpeedDomain> {
    public HighSpeedRepository(RedisConnectionFactory redisConnectionFactory, RedisTemplate<String, String> stringRedisTemplate) {
        super(redisConnectionFactory, stringRedisTemplate,HighSpeedDomain.class.getSimpleName(), HighSpeedDomain::getId);
    }
}
