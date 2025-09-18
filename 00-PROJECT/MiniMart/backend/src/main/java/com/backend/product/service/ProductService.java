package com.backend.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.product.dto.req.ProductUpdateReq;
import com.backend.product.dto.res.ProductDTO;
import com.backend.product.dto.res.ProductDetailDTO;

public interface ProductService {
    Page<ProductDTO> findByCategoryId(long categoryId, Pageable pageable);

    ProductDetailDTO findProductDetailById(long productId);

    Page<ProductDTO> findAdminAll(Pageable pageable);

    ProductDetailDTO findAdminDetailById(long productId);

    ProductDTO create(ProductUpdateReq dto);

    ProductDTO update(long productId, ProductUpdateReq dto);

    Long deleteById(long id);

}
