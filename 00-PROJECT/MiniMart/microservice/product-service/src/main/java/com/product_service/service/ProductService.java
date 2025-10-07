package com.product_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.product_service.dto.req.ProductUpdateReq;
import com.product_service.dto.res.ProductDetailDTO;
import com.product_service.dto.res.ProductSummaryDTO;

public interface ProductService {

    ProductDetailDTO findProductDetailById(long id);

    Page<ProductSummaryDTO> findByCategoryId(long categoryId, Pageable pageable);

    Page<ProductSummaryDTO> findAdminAll(Pageable pageable);

    ProductDetailDTO findAdminDetailById(long id);

    ProductSummaryDTO create(ProductUpdateReq dto);

    ProductSummaryDTO update(long productId, ProductUpdateReq dto);

    Long deleteById(long id);

    ProductDetailDTO findProductDetailByIdWithOutRelate(long id);
}
