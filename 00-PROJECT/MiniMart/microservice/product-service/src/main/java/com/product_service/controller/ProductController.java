package com.product_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.product_service.dto.res.ProductSummaryDTO;
import com.product_service.dto.res.ProductDetailDTO;
import com.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @GetMapping("/category/{categoryId}")
    public Page<ProductSummaryDTO> findPageByCategoryId(@PathVariable long categoryId,
            @PageableDefault Pageable pageable) {
        return service.findByCategoryId(categoryId, pageable);
    }

    @GetMapping("{id}")
    public ProductDetailDTO findDetailById(@PathVariable long id) {
        return service.findProductDetailById(id);
    }

}
