package net.devnguyen.common.redis;

import org.springframework.cache.CacheManager;

public interface CacheManagerFactory {
    CacheManager createCacheManager();
}
