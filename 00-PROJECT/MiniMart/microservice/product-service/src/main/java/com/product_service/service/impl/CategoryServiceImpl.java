package com.product_service.service.impl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.exception.GenericException;
import com.product_service.dto.req.CategoryFilter;
import com.product_service.dto.req.CategoryUpdateReq;
import com.product_service.dto.res.CategoryAdminDTO;
import com.product_service.dto.res.CategoryAdminDetailDTO;
import com.product_service.dto.res.CategoryDetailDTO;
import com.product_service.dto.res.CategorySummaryDTO;
import com.product_service.enums.ProductStatus;
import com.product_service.event.Action;
import com.product_service.event.CategoryEvent;
import com.product_service.exception.ErrorCode;
import com.product_service.mapper.CategoryMapper;
import com.product_service.model.Category;
import com.product_service.repository.CategoryRepository;
import com.product_service.repository.ProductRepository;
import com.product_service.service.CategoryService;
import com.product_service.utils.CacheUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    private final ProductRepository productRepository;

    private final CategoryMapper mapper;

    private final ApplicationEventPublisher eventPublisher;

    @Cacheable(cacheNames = CacheUtils.CATEGORY_SUMMARY,key = "'ACTIVE'")
    @Override
    public List<CategorySummaryDTO> findAll() {
        return repository.findClientDTOByStatus(ProductStatus.ACTIVE);
    }

    @Cacheable(cacheNames = CacheUtils.CATEGORY_DETAIL, key = "#id")
    @Override
    public CategoryDetailDTO findById(long id) {
        return repository.findClientDTOByIdAndStatus(id, ProductStatus.ACTIVE)
                .orElseThrow(() -> new GenericException(ErrorCode.CATEGORY_NOT_FOUND, id));

    }

    @Override
    public Page<CategoryAdminDTO> findAllAdmin(CategoryFilter filter, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllAdmin'");
    }

    @Override
    public CategoryAdminDetailDTO findAdminById(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAdminById'");
    }

    @Transactional
    @Override
    public CategoryAdminDetailDTO create(CategoryUpdateReq dto) {
        Category category = mapper.toEntity(dto);
        category = repository.save(category);
        eventPublisher.publishEvent(new CategoryEvent(category.getId(), Action.CREATED));

        return mapper.toDTO(category);
    }

    @Transactional
    @Override
    public CategoryAdminDetailDTO update(Long id, CategoryUpdateReq dto) {
        Category existed = repository.findById(id)
                .orElseThrow(() -> new GenericException(ErrorCode.CATEGORY_NOT_FOUND, id));
        mapper.update(existed, dto);
        existed = repository.save(existed);
        eventPublisher.publishEvent(new CategoryEvent(existed.getId(), Action.UPDATED));

        return mapper.toDTO(existed);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        if (productRepository.existsByCategoryId(id)) {
            throw new GenericException(ErrorCode.CATEGORY_HAS_PRODUCTS, id);
        }

        repository.deleteById(id);
        eventPublisher.publishEvent(new CategoryEvent(id, Action.DELETED));
    }

}
