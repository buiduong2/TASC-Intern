package com.backend.product.service;

import java.util.List;

public interface ProductCacheService {

    void evictProductDetailCache(long productId);

    void evictProductDetailCache(List<Long> productIds);

    void evictAllProductListCaches();

}
