package com.product_service.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.redis.connection.RedisZSetCommands.ZAddArgs;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import com.product_service.dto.res.ProductDetailDTO;
import com.product_service.dto.res.ProductDetailDTO.ProductRelateDTO;
import com.product_service.enums.ProductStatus;
import com.product_service.mapper.ProductMapper;
import com.product_service.model.Product;
import com.product_service.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductCacheManager {

    private final RedisTemplate<String, ProductDetailDTO> detailRedisTemplate;

    private final RedisTemplate<String, ProductRelateDTO> relateRedisTemplate;

    private final RedisTemplate<String, Long> idRedisTemplate;

    private final ProductRepository productRepository;

    private final RedisUtils redisUtils;

    private final ProductMapper mapper;

    public ProductDetailDTO getProductDetailDTO(long productId) {
        return detailRedisTemplate.opsForValue()
                .get(CacheUtils.getProductDetailKey(productId));
    }

    public List<ProductRelateDTO> getRandomRelateDTO(long productId, long categoryId, long count) {
        Set<Long> setIds = idRedisTemplate.opsForSet()
                .distinctRandomMembers(CacheUtils.getProductIdsByCategoryKey(categoryId), count + 1);

        if (setIds == null) {
            return null;
        }

        if (setIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> listIds = setIds.stream()
                .filter(id -> !Objects.equals(id, productId))
                .limit(count)
                .toList();

        List<ProductRelateDTO> cachedDTOs = relateRedisTemplate.opsForValue().multiGet(
                listIds.stream().map(CacheUtils::getProductRelateKey).toList()

        );
        if (cachedDTOs == null || cachedDTOs.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> missingIds = new ArrayList<>();
        List<ProductRelateDTO> result = new ArrayList<>();

        for (int i = 0; i < cachedDTOs.size(); i++) {
            if (cachedDTOs.get(i) == null) {
                missingIds.add(listIds.get(i));
            } else {
                result.add(cachedDTOs.get(i));
            }
        }

        if (missingIds.isEmpty()) {
            return result;
        }

        List<ProductRelateDTO> nonCacheDtos = new ArrayList<>();
        Map<Long, ProductRelateDTO> fallbackMap = productRepository.findRelatedByIdIn(missingIds, ProductStatus.ACTIVE)
                .stream()
                .collect(Collectors.toMap(ProductRelateDTO::getId, Function.identity()));

        for (Long missingId : missingIds) {
            if (fallbackMap.containsKey(missingId)) {
                ProductRelateDTO dto = fallbackMap.get(missingId);
                result.add(dto);
                nonCacheDtos.add(dto);
            }
        }

        putRelateDTOs(nonCacheDtos);

        return result;
    }

    public List<Long> getProductIdsShuffleByCategoryId(long categoryId) {
        Set<Long> shuffledProductIds = idRedisTemplate.opsForSet()
                .members(CacheUtils.getProductIdsByCategoryKey(categoryId));

        return shuffledProductIds == null
                ? null
                : shuffledProductIds.stream().toList();
    }

    public void putProductDetailDTO(ProductDetailDTO detailDTO) {
        detailRedisTemplate.opsForValue()
                .set(CacheUtils.getProductDetailKey(detailDTO.getId()), detailDTO, CacheUtils.PRODUCT_DETAIL_TTL);
    }

    public void putRelateDTO(ProductRelateDTO productRelateDTO) {
        relateRedisTemplate.opsForValue()
                .set(CacheUtils.getProductRelateKey(productRelateDTO.getId()), productRelateDTO,
                        CacheUtils.PRODUCT_RELATE_TTL);
    }

    public void putRelateDTOs(List<ProductRelateDTO> dtos) {
        Map<String, ProductRelateDTO> dtoMap = dtos.stream()
                .collect(Collectors.toMap(dto -> CacheUtils.getProductRelateKey(dto.getId()), Function.identity()));

        redisUtils.batchAddString(relateRedisTemplate, dtoMap, CacheUtils.PRODUCT_RELATE_TTL.getSeconds());
    }

    public void putCategoryId(long categoryId) {
        List<Long> productIds = productRepository.findProductIdByCategoryId(categoryId, ProductStatus.ACTIVE);
        putCategoryId(categoryId, productIds);
    }

    public void putCategoryId(long categoryId, List<Long> productIds) {
        redisUtils.batchAddSet(idRedisTemplate, Map.of(CacheUtils.getProductIdsByCategoryKey(categoryId), productIds),
                CacheUtils.PRODUCT_ID_BY_CATEGORY_TTL.toSeconds());
    }

    public void putProductById(long productId, long oldCategoryId) {
        Optional<Product> pOptional = productRepository.findClientDTOByIdAndStatus(productId, ProductStatus.ACTIVE);
        if (pOptional.isPresent()) {
            Product product = pOptional.get();
            if (product.getCategory().getId() != oldCategoryId) {
                idRedisTemplate.opsForSet().remove(CacheUtils.getProductIdsByCategoryKey(oldCategoryId),
                        product.getId());
                idRedisTemplate.opsForSet().add(CacheUtils.getProductIdsByCategoryKey(product.getCategory().getId()),
                        product.getId());
            }

            ProductDetailDTO detailDTO = mapper.toDetailDTO(product);
            ProductRelateDTO relateDTO = mapper.toRelateDTO(product);

            putRelateDTO(relateDTO);
            putProductDetailDTO(detailDTO);

        } else {
            detailRedisTemplate.delete(
                    List.of(CacheUtils.getProductDetailKey(productId), CacheUtils.getProductRelateKey(productId)));
        }
    }

    public void evictCategory(long categoryId) {
        idRedisTemplate.delete(CacheUtils.getProductIdsByCategoryKey(categoryId));
    }

    public void evictProduct(Long productId, long categoryId) {
        relateRedisTemplate.delete(CacheUtils.getProductDetailKey(categoryId));
        relateRedisTemplate.delete(CacheUtils.getProductRelateKey(categoryId));
        idRedisTemplate.opsForSet().remove(CacheUtils.getProductIdsByCategoryKey(categoryId), productId);
    }

    @SuppressWarnings({ "null", "unchecked" })
    public void addDirtyProductIdByTagChange(List<Long> productIds) {
        long score = System.currentTimeMillis();

        RedisSerializer<String> keySerializer = idRedisTemplate.getStringSerializer();
        RedisSerializer<Long> valueSerializer = (RedisSerializer<Long>) idRedisTemplate.getValueSerializer();

        ZAddArgs nxArgs = ZAddArgs.empty().nx();
        String dirtyKey = CacheUtils.getTagDirtyProductIdsKey();

        idRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            byte[] keyBytes = keySerializer.serialize(dirtyKey);
            for (Long productId : productIds) {
                byte[] memberBytes = valueSerializer.serialize(productId);
                connection.zSetCommands().zAdd(keyBytes, score, memberBytes, nxArgs);
            }
            return null;
        });
    }

}
