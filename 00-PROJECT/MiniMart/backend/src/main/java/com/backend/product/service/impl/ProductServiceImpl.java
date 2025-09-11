package com.backend.product.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.product.dto.event.ProductCreatedEvent;
import com.backend.product.dto.req.ProductUpdateReq;
import com.backend.product.dto.res.ProductDTO;
import com.backend.product.dto.res.ProductDetailDTO;
import com.backend.product.mapper.ProductMapper;
import com.backend.product.model.Product;
import com.backend.product.model.ProductStatus;
import com.backend.product.model.Tag;
import com.backend.product.repository.CategoryRepository;
import com.backend.product.repository.ProductRepository;
import com.backend.product.repository.TagRepository;
import com.backend.product.service.ProductService;

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
    public Page<ProductDTO> findByCategoryId(long categoryId, Pageable pageable) {
        return repository.findDTOByCategoryIdAndStatus(categoryId, ProductStatus.ACTIVE, pageable)
                .map(mapper::toDTO);
    }

    @Override
    public ProductDetailDTO findProductDetailById(long productId) {
        return repository.findDetailDTOByIdAndStatus(productId, ProductStatus.ACTIVE)
                .map(mapper::toDetailDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product ID = " + productId + " Not found"));
    }

    @Override
    public Page<ProductDTO> findAdminAll(Pageable pageable) {
        return repository.findAdminDTOBy(pageable)
                .map(mapper::toDTO);
    }

    @Transactional
    @Override
    public ProductDTO create(ProductUpdateReq dto) {
        Product product = mapper.toEntity(dto);
        List<Tag> tags = tagRepository.findAllById(dto.getTagIds());
        if (tags.size() != dto.getTagIds().size()) {
            throw new ResourceNotFoundException("Some of tagIds is missing ");
        }

        product.setCategory(categoryRepository.getReferenceById(dto.getCategoryId()));
        tags.forEach(product::addTag);

        product = repository.save(product);
        eventPublisher.publishEvent(new ProductCreatedEvent(product.getId()));
        return mapper.toDTO(product);
    }

    @Transactional
    @Override
    public ProductDTO update(long productId, ProductUpdateReq dto) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product id = " + productId + " is not found"));

        mapper.updateEntity(product, dto);

        if (!product.getCategory().getId().equals(dto.getCategoryId())) {
            product.setCategory(categoryRepository.getReferenceById(dto.getCategoryId()));
        }

        Set<Long> newTagIds = new HashSet<>(dto.getTagIds());
        Set<Tag> oldTags = product.getTags();
        Set<Long> requiredTagIds = dto.getTagIds();
        for (Tag oldTag : oldTags) {
            if (!requiredTagIds.contains(oldTag.getId())) {
                product.removeTag(oldTag);
            } else {
                newTagIds.add(oldTag.getId());
            }
        }

        List<Tag> tags = tagRepository.findAllById(newTagIds);

        tags.forEach(product::addTag);
        repository.save(product);

        return mapper.toDTO(product);
    }
}
