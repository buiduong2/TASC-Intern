package com.product_service.cache.utils;

import org.springframework.data.redis.cache.CacheKeyPrefix;

public class CacheKeyUtils {

    // product-service::product:detail::{productId} JSON
    public static final String PRODUCT_DETAIL = "product:detail";

    // product-service::product:ids:by-category::{categoryId} SET<ProductId>
    public static final String PRODUCT_ID_BY_CATEGORY = "product:ids:by-category";

    // product-service::product:relate::{productId} JSON
    public static final String PRODUCT_RELATE = "product:relate";

    // product-service::product:relate-ids:by-product::{productId} SET<RelateId>
    public static final String PRODUCT_RELATE_ID_BY_PRODUCT = "product:relate-ids:by-product";

    public static final CacheKeyPrefix cacheKeyPrefix = CacheKeyPrefix.prefixed("product-service::");

    public static String productDetailKey(long productId) {
        return computed(PRODUCT_DETAIL, productId);
    }

    public static String productIdsByCategoryKey(long categoryId) {
        return computed(PRODUCT_ID_BY_CATEGORY, categoryId);
    }

    public static String productRelateKey(long productId) {
        return computed(PRODUCT_RELATE, productId);
    }

    public static String productRelateIdsByProductKey(long productId) {
        return computed(PRODUCT_RELATE_ID_BY_PRODUCT, productId);
    }

    private static String computed(String cacheName, Object suffix) {
        return cacheKeyPrefix.compute(cacheName) + suffix;
    }
}
