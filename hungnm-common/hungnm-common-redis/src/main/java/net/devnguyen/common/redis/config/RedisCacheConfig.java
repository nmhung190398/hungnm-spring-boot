package net.devnguyen.common.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.cache")
public class RedisCacheConfig {
    private RedisCacheProps redis;
}
