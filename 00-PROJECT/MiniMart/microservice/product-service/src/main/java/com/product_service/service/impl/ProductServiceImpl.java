package com.product_service.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.exception.GenericException;
import com.product_service.dto.req.ProductUpdateReq;
import com.product_service.dto.res.ProductDetailDTO;
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
import com.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    private final CategoryRepository categoryRepository;

    private final TagRepository tagRepository;

    private final ProductMapper mapper;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Page<ProductSummaryDTO> findByCategoryId(long categoryId, Pageable pageable) {
        return repository.findForDTOByCategoryIdAndStatus(categoryId, ProductStatus.ACTIVE, pageable);
    }

    @Override
    public ProductDetailDTO findProductDetailById(long id) {
        return repository.findClientDTOByIdAndStatus(id, ProductStatus.ACTIVE)
                .map(mapper::toDetailDTO)
                .orElseThrow(() -> new GenericException(ErrorCode.PRODUCT_NOT_FOUND, id));
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

        product = repository.save(product);
        eventPublisher.publishEvent(new ProductEvent(product.getId(), Action.CREATED));
        return mapper.toDTO(product);
    }

    @Override
    public ProductSummaryDTO update(long productId, ProductUpdateReq dto) {
        Product product = repository.findForUpdateById(productId)
                .orElseThrow(() -> new GenericException(ErrorCode.PRODUCT_NOT_FOUND, productId));

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

        eventPublisher.publishEvent(new ProductEvent(productId, Action.UPDATED));

        return mapper.toDTO(product);

    }

    @Override
    public Long deleteById(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

}
