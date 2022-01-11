package net.devnguyen.common.redis.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.cache.CacheProperties;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class RedisCacheProps extends CacheProperties.Redis {
    public final static String JDK_VALUE_FORMAT = "jdk";
    public final static String JSON_VALUE_FORMAT = "json";

    // Value serialize, deserialization format
    private String valueFormat = JSON_VALUE_FORMAT;
    private Map<String, RedisCacheProps> cacheProps;
}
