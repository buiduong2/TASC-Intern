package com.product_service.service;

import java.util.List;

import com.product_service.dto.res.ProductDetailDTO.ProductRelateDTO;

public interface ProductRelateCacheService {

    List<ProductRelateDTO> getRandomRelateByProductIdAndCategoryId(long productId, long categoryId,long relateCount);

    List<Long> getShuffledProductIdsByCategory(long categoryId);

}
