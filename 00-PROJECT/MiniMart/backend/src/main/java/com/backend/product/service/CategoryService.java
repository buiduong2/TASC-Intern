package com.backend.product.service;

import java.util.List;

import com.backend.product.dto.res.CategoryDTO;
import com.backend.product.dto.res.CategoryDetailDTO;

public interface CategoryService {
    List<CategoryDTO> findAll();

    CategoryDetailDTO findById(long id);
}
