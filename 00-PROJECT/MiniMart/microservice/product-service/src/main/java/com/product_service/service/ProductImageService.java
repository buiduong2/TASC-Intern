package com.product_service.service;

import java.util.concurrent.CompletableFuture;

import com.product_service.dto.res.ImageMetaRes;

public interface ProductImageService {
    void save(ImageMetaRes res, long productId);

    CompletableFuture<String> delete(Long imageId);
}
