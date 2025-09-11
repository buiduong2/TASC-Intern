package com.backend.product.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.common.service.ImageCloudService;
import com.backend.common.service.impl.ImageCloudServiceImpl;
import com.backend.common.validation.Image;
import com.backend.product.dto.req.ProductUpdateReq;
import com.backend.product.dto.res.ProductDTO;
import com.backend.product.service.ProductImageService;
import com.backend.product.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/products")

@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService service;

    private final ImageCloudService imageCloudService;

    private final ProductImageService productImageService;

    @GetMapping
    public Page<ProductDTO> findAll(
            @PageableDefault(size = 10) Pageable pageable) {
        return service.findAdminAll(pageable);
    }

    @PostMapping()
    public ProductDTO create(
            @RequestPart("product") @Valid ProductUpdateReq dto,
            @RequestPart(name = "image", required = false) @Image MultipartFile image) {

        ProductDTO result = service.create(dto);
        if (image != null && !image.isEmpty()) {
            imageCloudService.updateImage(image, result.getId(), ImageCloudServiceImpl.PRODUCT_PREFIX)
                    .thenAccept(meta -> productImageService.save(meta, result.getId()));
        }
        return result;
    }

    @PutMapping("{id}")
    public ProductDTO update(@PathVariable long productId,
            @RequestPart("product") @Valid ProductUpdateReq dto,
            @RequestPart(name = "image", required = false) @Image MultipartFile image) {
        ProductDTO result = service.update(productId, dto);
        if (image != null && !image.isEmpty()) {
            imageCloudService.updateImage(image, result.getId(), ImageCloudServiceImpl.PRODUCT_PREFIX)
                    .thenAccept(meta -> productImageService.save(meta, result.getId()));
        }
        return result;
    }
}
