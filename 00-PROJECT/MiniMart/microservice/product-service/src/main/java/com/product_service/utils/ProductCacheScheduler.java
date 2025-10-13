package com.product_service.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.product_service.dto.res.ProductDetailDTO;
import com.product_service.dto.res.ProductDetailDTO.ProductRelateDTO;
import com.product_service.enums.ProductStatus;
import com.product_service.mapper.ProductMapper;
import com.product_service.model.Product;
import com.product_service.model.Tag;
import com.product_service.repository.CategoryRepository;
import com.product_service.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductCacheScheduler {

    private final RedisTemplate<String, ProductDetailDTO> detailRedisTemplate;

    private final RedisTemplate<String, ProductRelateDTO> relateRedisTemplate;

    private final RedisTemplate<String, Long> idRedisTemplate;

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final ProductMapper productMapper;

    private final RedisUtils redisUtils;

    @Autowired
    @Qualifier("backgroundCacheJobExecutor")
    private Executor cacheExecutor;

    @Autowired
    @Qualifier("asyncTaskExecutor")
    private Executor asyncTaskExecutor;

    @Value("${custom.cache.batch.product-size:100}")
    private int batchProductSize = 100;

    @Value("${custom.cache.batch.product-id-by-category-size:5}")
    private int batchProductIdByCategorySize = 5;

    @EventListener(ApplicationReadyEvent.class)
    public void onStartupPrewarm() {
        log.info("[CacheJob] Application started — triggering initial prewarm...");
        prewarmCache();
    }

    @Scheduled(fixedDelayString = "${custom.schedule.product.dirty-cache-evict.delay:300000}")
    public void processDirtyProductQueue() {
        final String DIRTY_KEY = CacheUtils.getTagDirtyProductIdsKey();
        final int BATCH_SIZE = 50;

        if (Boolean.FALSE.equals(idRedisTemplate.hasKey(DIRTY_KEY))) {
            return;
        }

        Set<Long> productIds = idRedisTemplate.opsForZSet().range(DIRTY_KEY, 0, BATCH_SIZE - 1);
        if (productIds == null || productIds.isEmpty()) {
            return;
        }

        RedisSerializer<String> keySerializer = idRedisTemplate.getStringSerializer();

        List<String> productDetailKeys = productIds.stream()
                .map(CacheUtils::getProductDetailKey)
                .toList();

        List<Object> deleteResults = idRedisTemplate.executePipelined((RedisCallback<?>) conn -> {
            for (String key : productDetailKeys) {
                byte[] keyBytes = keySerializer.serialize(key);
                conn.keyCommands().del(keyBytes);
            }
            return null;
        });

        List<Long> needRefreshIds = new ArrayList<>();
        int index = 0;
        for (Long productId : productIds) {
            Object result = deleteResults.get(index++);
            if (result != null && ((Number) result).longValue() > 0) {
                needRefreshIds.add(productId);
            }
        }

        if (needRefreshIds.isEmpty()) {
            log.debug("[DirtyCache] No valid keys deleted, skip refresh.");
            idRedisTemplate.opsForZSet().remove(DIRTY_KEY, productIds.toArray());
            return;
        }

        List<Product> products = productRepository.findSummaryByIdInAndStatus(needRefreshIds, ProductStatus.ACTIVE);
        Map<Long, Set<Tag>> tagsByProductId = productRepository
                .getTagsByIdIn(products.stream().map(Product::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(tagMap -> (Long) tagMap[0],
                        Collectors.mapping(tagMap -> (Tag) tagMap[1], Collectors.toSet())));

        products.forEach(product -> {
            product.setTags(tagsByProductId.getOrDefault(product.getId(), Collections.emptySet()));
        });

        prewarmProductDetail(products);

        idRedisTemplate.opsForZSet().remove(DIRTY_KEY, productIds.toArray());
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void scheduledPrewarm() {
        log.info("[CacheJob] Scheduled daily prewarm triggered (2AM).");
        prewarmCache();
    }

    private void prewarmCache() {
        log.info("[CacheJob] ======= Begin Cache Prewarm =======");
        asyncTaskExecutor.execute(this::prewarmProductIdByCategory);
        asyncTaskExecutor.execute(this::prewarmProducts);

    }

    private void prewarmProductIdByCategory() {
        StopWatch watch = new StopWatch();
        watch.start();
        log.info("[CacheJob][ProductIdByCategory] prewarmProductIdByCategory: BEGIN");
        log.info("[CacheJob][ProductIdByCategory] batchProductIdByCategorySize = {}", batchProductIdByCategorySize);

        long ttlSeconds = CacheUtils.PRODUCT_ID_BY_CATEGORY_TTL.getSeconds();
        List<Long> categoryIds = categoryRepository.getIdByStatus(ProductStatus.ACTIVE);
        int i = 0;
        int totalCategory = categoryIds.size();
        int totalProducts = 0;
        while (i < totalCategory) {
            Map<String, List<Long>> productIdsByCategoryId = categoryIds
                    .subList(i, Math.min(i + batchProductIdByCategorySize, totalCategory))
                    .parallelStream()
                    .collect(Collectors.toMap(CacheUtils::getProductIdsByCategoryKey,
                            id -> productRepository.findProductIdByCategoryId(id, ProductStatus.ACTIVE)));

            log.info("[CacheJob][ProductIdByCategory] Batch {} -> Prewarming {} categories",
                    i / batchProductIdByCategorySize,
                    productIdsByCategoryId.size());
            i += batchProductIdByCategorySize;

            totalProducts += redisUtils.batchAddSet(idRedisTemplate, productIdsByCategoryId, ttlSeconds);
        }

        watch.stop();
        log.info("[CacheJob][ProductIdByCategory] prewarmProductIdByCategory: END ({} categories, ~{} products, {} ms)",
                categoryIds.size(), totalProducts, watch.getTotalTimeMillis());

    }

    private void prewarmProducts() {
        log.info("[CacheJob][Product] prewarmProducts: BEGIN (batch size = {})", batchProductSize);

        StopWatch watch = new StopWatch();
        watch.start();
        Slice<Product> slice = null;

        int total = 0;
        int batchCount = 1;
        Pageable pageable = PageRequest.of(0, batchProductSize);
        while (true) {
            slice = getProductSlice(pageable);
            if (!slice.hasContent()) { // DB rỗng thì dừng
                break;
            }

            List<Product> products = slice.getContent();
            total += products.size();
            batchCount++;

            CompletableFuture<Void> detailF = CompletableFuture.runAsync(() -> prewarmProductDetail(products),
                    cacheExecutor);
            CompletableFuture<Void> relateF = CompletableFuture.runAsync(() -> prewarmProductRelate(products),
                    cacheExecutor);
            CompletableFuture.allOf(detailF, relateF).join();

            log.info("[CacheJob][Product] Batch {} -> prewarmed {} products (detail + relate)", batchCount,
                    products.size());
            if (!slice.hasNext()) {
                break;
            }
            pageable = slice.nextPageable();
        }

        if (total == 0) {
            log.warn("[CacheJob][Product] Product slice is empty for prewarm.");
            return;
        }

        watch.stop();
        log.info("[CacheJob][Product] prewarmProducts: END ({} total products, {} ms)", total,
                watch.getTotalTimeMillis());

    }

    private Slice<Product> getProductSlice(Pageable pageable) {
        Slice<Product> slice = productRepository.findSummaryByStatus(ProductStatus.ACTIVE, pageable);
        Map<Long, Set<Tag>> tagsByProductId = productRepository
                .getTagsByIdIn(slice.getContent().stream().map(Product::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(tagMap -> (Long) tagMap[0],
                        Collectors.mapping(tagMap -> (Tag) tagMap[1], Collectors.toSet())))

        ;

        slice.getContent().forEach(product -> {
            product.setTags(tagsByProductId.getOrDefault(product.getId(), Collections.emptySet()));
        });

        return slice;
    }

    private void prewarmProductRelate(List<Product> products) {
        Map<String, ProductRelateDTO> mapDtoByKey = productMapper.toRelateDTOs(products)
                .stream()
                .collect(Collectors.toMap(p -> CacheUtils.getProductRelateKey(p.getId()), Function.identity()));
        long ttlSeconds = CacheUtils.PRODUCT_RELATE_TTL.toSeconds();

        redisUtils.batchAddString(relateRedisTemplate, mapDtoByKey, ttlSeconds);
    }

    private void prewarmProductDetail(List<Product> products) {
        Map<String, ProductDetailDTO> mapDtoByKey = productMapper.toDetailDTOs(products)
                .stream()
                .collect(Collectors.toMap(p -> CacheUtils.getProductDetailKey(p.getId()), Function.identity()));
        long ttlSeconds = CacheUtils.PRODUCT_DETAIL_TTL.toSeconds();
        redisUtils.batchAddString(detailRedisTemplate, mapDtoByKey, ttlSeconds);
    }

}
