package com.backend.product.service.impl;

import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.backend.product.service.ProductCacheService;
import com.backend.product.utils.CacheKeys;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductCacheServiceImpl implements ProductCacheService {

    private final CacheManager cacheManager;

    @Override
    public void evictProductDetailCache(long productId) {
        Cache cache = cacheManager.getCache(CacheKeys.PRODUCT_DETAIL_NAME);
        if (cache != null) {
            cache.evict(CacheKeys.getProductDetailByIdKey(productId));
        }
    }

    @Override
    public void evictProductDetailCache(List<Long> productIds) {
        Cache cache = cacheManager.getCache(CacheKeys.PRODUCT_DETAIL_NAME);
        if (cache != null) {
            for (Long long1 : productIds) {

                cache.evict(CacheKeys.getProductDetailByIdKey(long1));
            }
        }
    }

    @Override
    public void evictAllProductListCaches() {
        Cache cache = cacheManager.getCache(CacheKeys.PRODUCT_PAGE_NAME);
        if (cache != null) {
            cache.clear();
        }
    }

}
