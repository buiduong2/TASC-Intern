package com.backend.product.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.product.dto.res.CategoryDTO;
import com.backend.product.dto.res.CategoryDetailDTO;
import com.backend.product.mapper.CategoryMapper;
import com.backend.product.model.Status;
import com.backend.product.repository.CategoryRepository;
import com.backend.product.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    private final CategoryMapper mapper;

    @Override
    public List<CategoryDTO> findAll() {
        return repository.findDTOByStatus(Status.ACTIVE)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public CategoryDetailDTO findById(long id) {
        return repository.findDetailDTOByIdAndStatus(id, Status.ACTIVE)
                .map(mapper::toDetailDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Category Id = " + id + " Not Found"));
    }

}
