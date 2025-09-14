package com.backend.product.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.product.dto.req.CategoryFilter;
import com.backend.product.dto.req.CategoryUpdateReq;
import com.backend.product.dto.res.CategoryAdminDTO;
import com.backend.product.dto.res.CategoryAdminDetailDTO;
import com.backend.product.dto.res.CategoryDTO;
import com.backend.product.dto.res.CategoryDetailDTO;
import com.backend.product.mapper.CategoryMapper;
import com.backend.product.model.Category;
import com.backend.product.model.ProductStatus;
import com.backend.product.repository.CategoryRepository;
import com.backend.product.repository.JdbcCategoryRepository;
import com.backend.product.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    private final JdbcCategoryRepository jdbcRepository;

    private final CategoryMapper mapper;

    @Override
    public List<CategoryDTO> findAll() {
        return repository.findDTOByStatus(ProductStatus.ACTIVE)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public CategoryDetailDTO findById(long id) {
        return repository.findDetailDTOByIdAndStatus(id, ProductStatus.ACTIVE)
                .map(mapper::toDetailDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Category Id = " + id + " Not Found"));
    }

    @Override
    public Page<CategoryAdminDTO> findAllAdmin(CategoryFilter filter, Pageable pageable) {
        return jdbcRepository.findAllAdmin(filter, pageable);
    }

    @Transactional
    @Override
    public CategoryAdminDetailDTO create(CategoryUpdateReq dto) {
        Category category = mapper.toEntity(dto);
        jdbcRepository.save(category);
        return mapper.toAdminDetailDTO(category);
    }

    @Transactional
    @Override
    public CategoryAdminDetailDTO update(Long id, CategoryUpdateReq dto) {
        Category existed = jdbcRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category id = " + id + " is not exists"));
        mapper.updateEntity(existed, dto);
        jdbcRepository.save(existed);
        return mapper.toAdminDetailDTO(existed);
    }

    @Override
    public CategoryAdminDetailDTO findAdminById(long id) {
        return repository.findById(id)
                .map(mapper::toAdminDetailDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Category id = " + id + " is not exists"));
    }

}
