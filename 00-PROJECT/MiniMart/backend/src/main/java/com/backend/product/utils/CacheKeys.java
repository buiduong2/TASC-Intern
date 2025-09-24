package com.backend.product.utils;

import org.springframework.data.domain.Pageable;

public class CacheKeys {

    public static final String PRODUCT_PAGE_NAME = "product-page-cache";
    public static final String PRODUCT_DETAIL_NAME = "product-detail-cache";

    public static String getProductsByCategoryKey(long categoryId, Pageable pageable) {
        return "ProductDTO By Category:" + categoryId + "_" + pageable.getPageNumber() + "_" + pageable.getPageSize()
                + "_" + pageable.getSort().toString();
    }

    public static String getProductDetailByIdKey(long productId) {
        return "ProductDetailDTO:" + productId;
    }
}
