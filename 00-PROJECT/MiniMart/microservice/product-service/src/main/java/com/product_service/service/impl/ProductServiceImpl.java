package com.product_service.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.product_service.dto.req.ProductUpdateReq;
import com.product_service.dto.res.ProductDTO;
import com.product_service.dto.res.ProductDetailDTO;
import com.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {@Override
    public ProductDetailDTO findProductDetailById(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findProductDetailById'");
    }

    @Override
    public Page<ProductDTO> findByCategoryId(long categoryId, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByCategoryId'");
    }

    @Override
    public Page<ProductDTO> findAdminAll(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAdminAll'");
    }

    @Override
    public ProductDetailDTO findAdminDetailById(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAdminDetailById'");
    }

    @Override
    public ProductDTO create(ProductUpdateReq dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public ProductDTO update(long productId, ProductUpdateReq dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public Long deleteById(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

}
