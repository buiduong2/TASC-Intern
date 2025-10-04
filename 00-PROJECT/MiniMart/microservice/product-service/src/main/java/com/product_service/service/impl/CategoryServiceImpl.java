package com.product_service.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.product_service.dto.req.CategoryFilter;
import com.product_service.dto.req.CategoryUpdateReq;
import com.product_service.dto.res.CategoryAdminDTO;
import com.product_service.dto.res.CategoryAdminDetailDTO;
import com.product_service.dto.res.CategoryDTO;
import com.product_service.dto.res.CategoryDetailDTO;
import com.product_service.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {@Override
    public List<CategoryDTO> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public CategoryDetailDTO findById(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
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

    @Override
    public CategoryAdminDetailDTO create(CategoryUpdateReq dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public CategoryAdminDetailDTO update(Long id, CategoryUpdateReq dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void deleteById(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

}
