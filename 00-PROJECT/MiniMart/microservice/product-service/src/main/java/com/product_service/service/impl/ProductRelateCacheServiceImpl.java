package com.product_service.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.product_service.cache.utils.CacheKeyUtils;
import com.product_service.dto.res.ProductDetailDTO.ProductRelateDTO;
import com.product_service.enums.ProductStatus;
import com.product_service.repository.ProductRepository;
import com.product_service.service.ProductRelateCacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductRelateCacheServiceImpl implements ProductRelateCacheService {

    private final ProductRepository repository;

    private final ObjectProvider<ProductRelateCacheService> selfProvider;

    private final int relatedSize = 5;

    private final RedisTemplate<String, Object> redisTemplate;

    private ProductRelateCacheService self() {
        ProductRelateCacheService self = selfProvider.getIfAvailable();
        return self == null ? this : self;
    }

    @Override
    public List<ProductRelateDTO> getRandomRelateByProductIdAndCategoryId(long productId, long categoryId) {
        List<Long> relateIds = self().getRelateIds(productId, categoryId);
        List<ProductRelateDTO> result = getRelateByIdIn(relateIds);
        return result;
    }

    private List<ProductRelateDTO> getRelateByIdIn(List<Long> relateIds) {
        List<String> relateKeys = relateIds.stream().map(CacheKeyUtils::productRelateKey).toList();
        List<Object> cachedObjs = redisTemplate.opsForValue().multiGet(relateKeys);
        List<ProductRelateDTO> result = new ArrayList<>();
        List<ProductRelateDTO> nonCached;
        if (cachedObjs != null) {
            Map<Long, Integer> missingKeyByIndex = new HashMap<>();
            for (int i = 0; i < cachedObjs.size(); i++) {
                Object cachedObj = cachedObjs.get(i);
                if (cachedObj == null) {
                    missingKeyByIndex.put(relateIds.get(i), i);
                    result.add(null);
                } else {
                    result.add((ProductRelateDTO) cachedObj);
                }
            }

            if (!missingKeyByIndex.isEmpty()) {
                nonCached = repository.findRelatedByIdIn(missingKeyByIndex.keySet(), ProductStatus.ACTIVE);
                for (ProductRelateDTO relate : nonCached) {
                    result.set(missingKeyByIndex.get(relate.getId()), relate);
                }
            } else {
                nonCached = Collections.emptyList();
            }

        } else {
            nonCached = repository.findRelatedByIdIn(relateIds, ProductStatus.ACTIVE);
            result = nonCached;
        }

        self().cacheRelateDTO(nonCached);
        // List<ProductRelateDTO> result = repository.findRelatedByIdIn(relateIds);
        return result;
    }

    @Async
    @Override
    public void cacheRelateDTO(List<ProductRelateDTO> dtos) {
        if (dtos.isEmpty()) {
            return;
        }
        Map<String, ProductRelateDTO> mapByKeys = dtos.stream()
                .collect(Collectors.toMap(dto -> CacheKeyUtils.productRelateKey(dto.getId()), Function.identity()));
        redisTemplate.opsForValue().multiSet(mapByKeys);
    }

    @Cacheable(cacheNames = CacheKeyUtils.PRODUCT_RELATE_ID_BY_PRODUCT, key = "#productId")
    public List<Long> getRelateIds(long productId, long categoryId) {
        List<Long> allIds = self().getShuffledProductIdsByCategory(categoryId);
        int total = allIds.size();

        if (total <= 1) {
            return Collections.emptyList();
        }

        int actual = Math.min(relatedSize, total - 1);
        int start = ThreadLocalRandom.current().nextInt(0, total - actual);

        return new ArrayList<Long>(
                allIds.stream()
                        .skip(start)
                        .limit(actual + 1)
                        .filter(id -> id != productId)
                        .limit(actual)
                        .toList());
    }

    @Cacheable(cacheNames = CacheKeyUtils.PRODUCT_ID_BY_CATEGORY, key = "#categoryId")
    @Override
    public List<Long> getShuffledProductIdsByCategory(long categoryId) {
        List<Long> allIds = repository.findProductIdByCategoryId(categoryId, ProductStatus.ACTIVE);
        Collections.shuffle(allIds);
        return allIds;
    }

}
