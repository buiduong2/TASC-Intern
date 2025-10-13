package com.product_service.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.product_service.dto.res.ProductDetailDTO.ProductRelateDTO;
import com.product_service.enums.ProductStatus;
import com.product_service.repository.ProductRepository;
import com.product_service.service.ProductRelateCacheService;
import com.product_service.utils.ProductCacheManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductRelateCacheServiceImpl implements ProductRelateCacheService {

    private final ProductRepository repository;

    private final ProductCacheManager productCacheManager;

    @Override
    public List<ProductRelateDTO> getRandomRelateByProductIdAndCategoryId(long productId, long categoryId,
            long relateCount) {

        List<ProductRelateDTO> dtos = productCacheManager.getRandomRelateDTO(productId, categoryId, relateCount);
        if (dtos != null) {
            return dtos;
        }

        List<Long> shuffledIds = getShuffledProductIdsByCategory(categoryId);

        if (shuffledIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> productIds = shuffledIds.subList(0, (int) Math.min(relateCount, shuffledIds.size()));

        dtos = repository.findRelatedByIdIn(productIds, ProductStatus.ACTIVE);

        productCacheManager.putRelateDTOs(dtos);

        return dtos;
    }

    @Override
    public List<Long> getShuffledProductIdsByCategory(long categoryId) {

        List<Long> shuffledIds = productCacheManager.getProductIdsShuffleByCategoryId(categoryId);
        if (shuffledIds == null) {
            shuffledIds = repository.findProductIdByCategoryId(categoryId, ProductStatus.ACTIVE);
            Collections.shuffle(shuffledIds);

            productCacheManager.putCategoryId(categoryId, shuffledIds);
        }

        return shuffledIds;
    }

}
