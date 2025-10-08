package com.product_service.utils;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CacheEvictor {
    private final CacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;

    public void evictProductRelateDTO(long productId) {
        redisTemplate.delete(CacheUtils.getProductRelateKey(productId));
    }

    public void evictProductIdByCategory(long categoryId) {
        Cache productIdByCategoryCache = cacheManager.getCache(CacheUtils.PRODUCT_ID_BY_CATEGORY);
        if (productIdByCategoryCache != null) {
            productIdByCategoryCache.evict(categoryId);
        }
    }

    public void evictRelateIdsByProduct(long productId) {
        Cache productRelateId = cacheManager.getCache(CacheUtils.PRODUCT_RELATE_ID_BY_PRODUCT);
        if (productRelateId != null) {
            productRelateId.evict(productId);
        }
    }

    public void evictProductDetail(long productId) {
        Cache productDetailcache = cacheManager.getCache(CacheUtils.PRODUCT_DETAIL);
        if (productDetailcache != null) {
            productDetailcache.evict(productId);
        }
    }

    public void evictCategorySummaryList() {
        Cache categorySummaryCache = cacheManager.getCache(CacheUtils.CATEGORY_SUMMARY);
        if (categorySummaryCache != null) {
            categorySummaryCache.evict("ACTIVE");
        }
    }

    public void evictCategoryDetail(long categoryId) {
        Cache categoryDetailCache = cacheManager.getCache(CacheUtils.CATEGORY_DETAIL);
        if (categoryDetailCache != null) {
            categoryDetailCache.evict(categoryId);
        }
    }

}
