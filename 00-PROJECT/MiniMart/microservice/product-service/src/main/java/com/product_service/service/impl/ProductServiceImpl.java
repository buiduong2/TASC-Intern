package com.product_service.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.exception.GenericException;
import com.product_service.dto.req.ProductUpdateReq;
import com.product_service.dto.res.ProductDetailDTO;
import com.product_service.dto.res.ProductDetailDTO.ProductRelateDTO;
import com.product_service.dto.res.ProductSummaryDTO;
import com.product_service.enums.ProductStatus;
import com.product_service.event.Action;
import com.product_service.event.ProductEvent;
import com.product_service.exception.ErrorCode;
import com.product_service.mapper.ProductMapper;
import com.product_service.model.Product;
import com.product_service.model.Tag;
import com.product_service.repository.CategoryRepository;
import com.product_service.repository.ProductRepository;
import com.product_service.repository.TagRepository;
import com.product_service.service.ProductRelateCacheService;
import com.product_service.service.ProductService;
import com.product_service.utils.CacheUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    private final CategoryRepository categoryRepository;

    private final TagRepository tagRepository;

    private final ProductMapper mapper;

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    @Lazy
    private ObjectProvider<ProductService> selfProfProvider;

    private final ProductRelateCacheService relateCacheService;

    @Override
    public Page<ProductSummaryDTO> findByCategoryId(long categoryId, Pageable pageable) {
        return repository.findForDTOByCategoryIdAndStatus(categoryId, ProductStatus.ACTIVE, pageable);
    }

    private ProductService self() {
        ProductService self = selfProfProvider.getIfAvailable();
        return self == null ? this : self;
    }

    @Override
    public ProductDetailDTO findProductDetailById(long id) {

        ProductDetailDTO dto = self().findProductDetailByIdWithOutRelate(id);
        List<ProductRelateDTO> relates = relateCacheService.getRandomRelateByProductIdAndCategoryId(id,
                dto.getCategoryId());

        dto.setRelates(relates);
        return dto;
    }

    @Cacheable(cacheNames = CacheUtils.PRODUCT_DETAIL, key = "#id")
    @Override
    public ProductDetailDTO findProductDetailByIdWithOutRelate(long id) {
        return repository.findClientDTOByIdAndStatus(id, ProductStatus.ACTIVE)
                .map(mapper::toDetailDTO)
                .orElseThrow(() -> new GenericException(ErrorCode.PRODUCT_NOT_FOUND, id));
    }

    @Override
    public List<ProductDetailDTO> findProductDetailByIdInWithOutRelateNonCache(List<Long> ids) {
        return repository.findClientDTOByIdInAndStatus(ids, ProductStatus.ACTIVE)
                .stream()
                .map(mapper::toDetailDTO)
                .toList();
    }

    @Override
    public Page<ProductSummaryDTO> findAdminAll(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAdminAll'");
    }

    @Override
    public ProductDetailDTO findAdminDetailById(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAdminDetailById'");
    }

    @Transactional
    @Override
    public ProductSummaryDTO create(ProductUpdateReq dto) {
        Product product = mapper.toEntity(dto);
        List<Tag> tags = tagRepository.findAllById(dto.getTagIds());

        if (tags.size() != dto.getTagIds().size()) {
            throw new GenericException(ErrorCode.TAG_SOME_NOT_FOUND);
        }

        product.setCategory(categoryRepository.getReferenceById(dto.getCategoryId()));

        tags.forEach(product::addTag);

        eventPublisher
                .publishEvent(new ProductEvent(product.getId(), product.getCategory().getId(), null, Action.CREATED));
        product = repository.save(product);
        return mapper.toDTO(product);
    }

    @Override
    public ProductSummaryDTO update(long productId, ProductUpdateReq dto) {
        Product product = repository.findForUpdateById(productId)
                .orElseThrow(() -> new GenericException(ErrorCode.PRODUCT_NOT_FOUND, productId));

        // Category
        long oldCategoryId = product.getCategory().getId();
        if (oldCategoryId != dto.getCategoryId()) {
            product.setCategory(categoryRepository.getReferenceById(dto.getCategoryId()));
        }
        // Tag
        Set<Long> newTagIds = new HashSet<>(dto.getTagIds());
        Set<Tag> oldTags = new HashSet<>(product.getTags());
        Set<Long> requiredTagIds = dto.getTagIds();
        Set<Tag> toRemoveTags = new HashSet<>();

        for (Tag oldTag : oldTags) {
            if (!requiredTagIds.contains(oldTag.getId())) {
                toRemoveTags.add(oldTag);
            } else {
                newTagIds.add(oldTag.getId());
            }
        }

        List<Tag> toAddTags = tagRepository.findAllById(newTagIds);

        toRemoveTags.forEach(product::removeTag);
        toAddTags.forEach(product::addTag);
        product = repository.save(product);

        eventPublisher.publishEvent(
                new ProductEvent(product.getId(), product.getCategory().getId(), oldCategoryId, Action.UPDATED));

        return mapper.toDTO(product);

    }

    @Override
    public Long deleteById(long id) {
        // TODO Auto-generated method stub

        Product product = repository.findById(id)
                .orElseThrow(() -> new GenericException(ErrorCode.PRODUCT_NOT_FOUND, id));

        Long categoryId = product.getCategory().getId();
        eventPublisher.publishEvent(
                new ProductEvent(product.getId(), categoryId, categoryId, Action.DELETED));
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

}
