package com.product_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.product_service.dto.req.ProductCheckExistsReq;
import com.product_service.dto.res.ProductCheckExistsRes;
import com.product_service.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/internal/products")
@RequiredArgsConstructor
public class InternalProductController {

    private final ProductService service;

    @PostMapping("/validate-existence")
    public ProductCheckExistsRes checkProductExsitsByIds(@Valid @RequestBody ProductCheckExistsReq req) {
        return service.validateExistsByIdsForInternal(req);
    }

}
