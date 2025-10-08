package com.product_service.utils;

import java.time.Duration;

import org.springframework.data.redis.cache.CacheKeyPrefix;

public class CacheUtils {

    // product-service::product:detail::{productId} JSON
    public static final String PRODUCT_DETAIL = "product:detail";
    public static final Duration PRODUCT_DETAIL_TTL = Duration.ofHours(12);

    // product-service::product:ids:by-category::{categoryId} JSON
    public static final String PRODUCT_ID_BY_CATEGORY = "product:ids:by-category";
    public static final Duration PRODUCT_ID_BY_CATEGORY_TTL = Duration.ofDays(1);

    // product-service::product:relate::{productId} JSON
    private static final String PRODUCT_RELATE = "product:relate";
    public static final Duration PRODUCT_RELATE_TTL = Duration.ofHours(12);
    // product-service::product:relate-ids:by-product::{productId} JSON
    public static final String PRODUCT_RELATE_ID_BY_PRODUCT = "product:relate-ids:by-product";
    public static final Duration PRODUCT_RELATE_ID_BY_PRODUCT_TTL = Duration.ofDays(1);

    // product-service::tag::dirty-product-ids ZSET<LONG>
    private static final String TAG_DIRTY_PRODUCT_ID = "tag:dirty-product-ids";
    public static final Duration TAG_DIRTY_PRODUCT_ID_TTL = Duration.ofHours(12);

    // product-service::category:summary JSON
    public static final String CATEGORY_SUMMARY = "category:summary";
    public static final Duration CATEGORY_SUMMARY_TTL = Duration.ofHours(12);

    // product-service::category:detail:{id} JSON
    public static final String CATEGORY_DETAIL = "category:detail";
    public static final Duration CATEGORY_DETAIL_TTL = Duration.ofHours(12);

    public static final CacheKeyPrefix cacheKeyPrefix = CacheKeyPrefix.prefixed("product-service::");

    public static String getProductDetailKey(long productId) {
        return computed(PRODUCT_DETAIL, productId);
    }

    public static String getProductIdsByCategoryKey(long categoryId) {
        return computed(PRODUCT_ID_BY_CATEGORY, categoryId);
    }

    public static String getProductRelateKey(long productId) {
        return computed(PRODUCT_RELATE, productId);
    }

    public static String getProductRelateIdsByProductKey(long productId) {
        return computed(PRODUCT_RELATE_ID_BY_PRODUCT, productId);
    }

    public static String getTagDirtyProductIdsKey() {
        return cacheKeyPrefix.compute(TAG_DIRTY_PRODUCT_ID);
    }

    private static String computed(String cacheName, Object suffix) {
        return cacheKeyPrefix.compute(cacheName) + suffix;
    }
}
