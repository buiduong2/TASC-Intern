package com.product_service.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.connection.RedisZSetCommands.ZAddArgs;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.product_service.dto.res.ProductDetailDTO;
import com.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductCacheEvictor {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ProductService productService;

    private static final int BATCH_SIZE = 30;
    private static final String DIRTY_KEY = CacheUtils.getTagDirtyProductIdsKey();

    @SuppressWarnings({ "null", "unchecked" })
    public void addDirtyProductIdByTagChange(List<Long> productIds) {
        long score = System.currentTimeMillis();
        RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
        RedisSerializer<Object> valueSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();

        ZAddArgs nxArg = ZAddArgs.empty().nx();
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            byte[] keyBytes = keySerializer.serialize(DIRTY_KEY);

            for (Long productId : productIds) {
                byte[] memberBytes = valueSerializer.serialize(productId);
                connection.zSetCommands().zAdd(keyBytes, score, memberBytes, nxArg);
            }
            return null;
        });
    }

    @SuppressWarnings({ "unchecked", "null" })
    @Scheduled(fixedDelayString = "${custom.schedule.product.dirty-cache-evict.delay:300000}")
    public void processDirtyProductQueue() {

        if (Boolean.FALSE.equals(redisTemplate.hasKey(DIRTY_KEY))) {
            return;
        }

        Set<Object> rawDatas = redisTemplate.opsForZSet().range(DIRTY_KEY, 0, BATCH_SIZE - 1);
        if (rawDatas == null || rawDatas.isEmpty()) {
            return;
        }

        List<Long> productIds = rawDatas.stream().filter(Objects::nonNull).map(r -> ((Number) r).longValue()).toList();
        List<String> productDetailKeys = productIds.stream().map(CacheUtils::getProductDetailKey).toList();
        RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
        RedisSerializer<Object> valuSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();

        List<Object> deleteProductDetailResults = redisTemplate.executePipelined((RedisCallback<?>) conn -> {

            for (String key : productDetailKeys) {
                byte[] keyBytes = keySerializer.serialize(key);
                conn.keyCommands().del(keyBytes);
            }
            return null;
        });

        List<Long> needRefreshIds = new ArrayList<>();

        for (int i = 0; i < productIds.size(); i++) {
            Object result = deleteProductDetailResults.get(i);
            if (result == null || ((Number) result).longValue() == 0) {
                continue;
            }
            needRefreshIds.add(productIds.get(i));
        }

        List<ProductDetailDTO> productDetailDTOs = productService
                .findProductDetailByIdInWithOutRelateNonCache(needRefreshIds);

        redisTemplate.executePipelined((RedisCallback<?>) conn -> {

            for (ProductDetailDTO dto : productDetailDTOs) {
                byte[] keyBytes = keySerializer.serialize(CacheUtils.getProductDetailKey(dto.getId()));
                byte[] values = valuSerializer.serialize(dto);

                conn.stringCommands().set(keyBytes, values, Expiration.from(CacheUtils.PRODUCT_DETAIL_TTL),
                        SetOption.upsert());
            }
            return null;
        });

        redisTemplate.opsForZSet().remove(DIRTY_KEY, productIds.toArray());

    }

}
