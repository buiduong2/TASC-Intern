package com.backend.common.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.backend.product.utils.CacheKeys;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        List<ConcurrentMapCache> caches = Arrays.asList(
                new ConcurrentMapCache(CacheKeys.PRODUCT_PAGE_NAME),
                new ConcurrentMapCache(CacheKeys.PRODUCT_DETAIL_NAME),
                new ConcurrentMapCache("category-cache")

        );
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }
}
