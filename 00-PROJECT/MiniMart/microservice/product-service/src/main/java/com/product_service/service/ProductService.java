package com.product_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.product_service.dto.req.ProductUpdateReq;
import com.product_service.dto.res.ProductDTO;
import com.product_service.dto.res.ProductDetailDTO;

public interface ProductService {

    ProductDetailDTO findProductDetailById(long id);

    Page<ProductDTO> findByCategoryId(long categoryId, Pageable pageable);

    Page<ProductDTO> findAdminAll(Pageable pageable);

    ProductDetailDTO findAdminDetailById(long id);

    ProductDTO create(ProductUpdateReq dto);

    ProductDTO update(long productId, ProductUpdateReq dto);

    Long deleteById(long id);

}
