package com.backend.product.service;

import java.util.concurrent.CompletableFuture;

import com.backend.common.dto.ImageMetaRes;

public interface ProductImageService {
    void save(ImageMetaRes res, long productId);

    CompletableFuture<String> delete(Long imageId);
}
