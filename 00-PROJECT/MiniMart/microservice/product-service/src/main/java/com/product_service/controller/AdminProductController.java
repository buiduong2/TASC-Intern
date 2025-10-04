package com.product_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.common.validation.Image;
import com.product_service.dto.req.ProductUpdateReq;
import com.product_service.dto.res.ProductDTO;
import com.product_service.dto.res.ProductDetailDTO;
import com.product_service.service.ImageCloudService;
import com.product_service.service.ProductImageService;
import com.product_service.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/products")
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

    @GetMapping("{id}")
    public ProductDetailDTO findById(@PathVariable long id) {
        return service.findAdminDetailById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductDTO create(
            @RequestPart("product") @Valid ProductUpdateReq dto,
            @RequestPart(name = "image", required = false) @Image MultipartFile image) {

        ProductDTO result = service.create(dto);
        if (image != null && !image.isEmpty()) {
            imageCloudService.updateImage(image, result.getId(), ImageCloudService.PRODUCT_PREFIX)
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
            imageCloudService.updateImage(image, result.getId(), ImageCloudService.PRODUCT_PREFIX)
                    .thenAccept(meta -> productImageService.save(meta, result.getId()));
        }
        return result;
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable long id) {
        Long imageId = service.deleteById(id);

        productImageService.delete(imageId)
                .thenAccept(
                        publicId -> imageCloudService.deleteImageForId(ImageCloudService.PRODUCT_PREFIX, publicId));

    }
}
