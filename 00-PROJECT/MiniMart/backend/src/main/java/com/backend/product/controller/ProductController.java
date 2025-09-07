package com.backend.product.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.product.dto.res.ProductDTO;
import com.backend.product.dto.res.ProductDetailDTO;
import com.backend.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @GetMapping("{id}")
    public ProductDetailDTO findDetailById(@PathVariable long id) {
        return service.findProductDetailById(id);
    }

    @GetMapping("/category/{categoryId}")
    public Page<ProductDTO> findPageByCategoryId(@PathVariable long categoryId,
            @PageableDefault Pageable pageable) {
        return service.findByCategoryId(categoryId, pageable);
    }

}
