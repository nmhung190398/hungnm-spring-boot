package net.devnguyen.common.redis;

import lombok.extern.slf4j.Slf4j;
import net.devnguyen.common.redis.config.RedisCacheConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
@EnableCaching
@EnableConfigurationProperties({RedisCacheConfig.class,RedisProperties.class})
@Slf4j
public class CacheAutoConfiguration extends CachingConfigurerSupport {

    @Bean(name = CacheManagerNames.REDIS_CACHE_MANAGER)
    public CacheManager redisCacheManager(@Lazy
            RedisConnectionFactory redisConnectionFactory, RedisCacheConfig redisCacheConfig) {
        CacheManagerFactory cacheManagerFactory = new RedisCacheManagerFactory(redisConnectionFactory, redisCacheConfig);
        return cacheManagerFactory.createCacheManager();
    }



    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.error("Failure getting from cache: " + cache.getName() + ", exception: " + exception.toString());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.error("Failure putting into cache: " + cache.getName() + ", exception: " + exception.toString());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.error("Failure evicting from cache: " + cache.getName() + ", exception: " + exception.toString());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.error("Failure clearing cache: " + cache.getName() + ", exception: " + exception.toString());
            }
        };
    }
}
