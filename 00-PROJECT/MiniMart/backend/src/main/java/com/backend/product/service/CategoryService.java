package com.backend.product.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.product.dto.req.CategoryFilter;
import com.backend.product.dto.req.CategoryUpdateReq;
import com.backend.product.dto.res.CategoryAdminDTO;
import com.backend.product.dto.res.CategoryAdminDetailDTO;
import com.backend.product.dto.res.CategoryDTO;
import com.backend.product.dto.res.CategoryDetailDTO;

public interface CategoryService {
    List<CategoryDTO> findAll();

    CategoryDetailDTO findById(long id);

    Page<CategoryAdminDTO> findAllAdmin(CategoryFilter filter, Pageable pageable);

    CategoryAdminDetailDTO create(CategoryUpdateReq dto);

    CategoryAdminDetailDTO update(Long id, CategoryUpdateReq dto);

    CategoryAdminDetailDTO findAdminById(long id);
}
