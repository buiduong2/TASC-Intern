package com.product_service.service;

import java.util.List;

import com.product_service.dto.res.ProductDetailDTO.ProductRelateDTO;

public interface ProductRelateCacheService  {

    List<ProductRelateDTO> getRandomRelateByProductIdAndCategoryId(long productId, long categoryId);

    List<Long> getShuffledProductIdsByCategory(long categoryId);

    List<Long> getRelateIds(long productId, long categoryId);

    void cacheRelateDTO(List<ProductRelateDTO> dtos);
    
}
