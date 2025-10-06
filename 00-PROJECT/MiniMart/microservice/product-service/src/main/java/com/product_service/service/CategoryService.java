package com.product_service.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.product_service.dto.req.CategoryFilter;
import com.product_service.dto.req.CategoryUpdateReq;
import com.product_service.dto.res.CategoryAdminDTO;
import com.product_service.dto.res.CategoryAdminDetailDTO;
import com.product_service.dto.res.CategorySummaryDTO;
import com.product_service.dto.res.CategoryDetailDTO;

public interface CategoryService {

    List<CategorySummaryDTO> findAll();

    CategoryDetailDTO findById(long id);

    Page<CategoryAdminDTO> findAllAdmin(CategoryFilter filter, Pageable pageable);

    CategoryAdminDetailDTO findAdminById(long id);

    CategoryAdminDetailDTO create(CategoryUpdateReq dto);

    CategoryAdminDetailDTO update(Long id, CategoryUpdateReq dto);

    void deleteById(long id);

}
