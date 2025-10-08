package com.product_service.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.product_service.dto.res.ProductDetailDTO.ProductRelateDTO;
import com.product_service.enums.ProductStatus;
import com.product_service.repository.ProductRepository;
import com.product_service.service.ProductRelateCacheService;
import com.product_service.utils.CacheUtils;

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
        List<ProductRelateDTO> result = getRelateByIdIn(relateIds, productId);
        return result;
    }

    private List<ProductRelateDTO> getRelateByIdIn(List<Long> relateIds, long productId) {
        List<String> relateKeys = relateIds.stream().map(CacheUtils::getProductRelateKey).toList();
        List<Object> cachedObjs = redisTemplate.opsForValue().multiGet(relateKeys);
        List<ProductRelateDTO> result = new ArrayList<>();
        List<ProductRelateDTO> nonCached;
        if (cachedObjs != null) {
            List<Long> missingIds = new ArrayList<>();
            for (int i = 0; i < cachedObjs.size(); i++) {
                Object cachedObj = cachedObjs.get(i);
                if (cachedObj == null) {
                    missingIds.add(relateIds.get(i));
                } else {
                    result.add((ProductRelateDTO) cachedObj);
                }
            }

            if (!missingIds.isEmpty()) {
                nonCached = repository.findRelatedByIdIn(missingIds, ProductStatus.ACTIVE);
                for (ProductRelateDTO relate : nonCached) {
                    result.add(relate);
                }
            } else {
                nonCached = Collections.emptyList();
            }

        } else {
            nonCached = repository.findRelatedByIdIn(relateIds, ProductStatus.ACTIVE);
            result = nonCached;
        }

        self().cacheRelateDTO(nonCached);

        if (result.size() < relateIds.size()) {
            // Detect stale relateIds ... rebuild
            redisTemplate.delete(CacheUtils.getProductRelateIdsByProductKey(productId));
        }

        // List<ProductRelateDTO> result = repository.findRelatedByIdIn(relateIds);
        return result;
    }

    @SuppressWarnings({ "unchecked", "null" })
    @Async
    @Override
    public void cacheRelateDTO(List<ProductRelateDTO> dtos) {
        if (dtos.isEmpty()) {
            return;
        }
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
            RedisSerializer<Object> valueSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();
            Map<String, ProductRelateDTO> mapByKeys = dtos.stream()
                    .collect(Collectors.toMap(dto -> CacheUtils.getProductRelateKey(dto.getId()),
                            Function.identity()));
            for (Map.Entry<String, ProductRelateDTO> entry : mapByKeys.entrySet()) {
                byte[] rawKey = keySerializer.serialize(entry.getKey());
                byte[] rawValue = valueSerializer.serialize(entry.getValue());

                connection.stringCommands().set(
                        rawKey,
                        rawValue,
                        Expiration.from(CacheUtils.PRODUCT_RELATE_TTL),
                        RedisStringCommands.SetOption.upsert());
            }
            return null;
        });

    }

    @Cacheable(cacheNames = CacheUtils.PRODUCT_RELATE_ID_BY_PRODUCT, key = "#productId")
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

    @Cacheable(cacheNames = CacheUtils.PRODUCT_ID_BY_CATEGORY, key = "#categoryId")
    @Override
    public List<Long> getShuffledProductIdsByCategory(long categoryId) {
        List<Long> allIds = repository.findProductIdByCategoryId(categoryId, ProductStatus.ACTIVE);
        Collections.shuffle(allIds);
        return allIds;
    }

}
