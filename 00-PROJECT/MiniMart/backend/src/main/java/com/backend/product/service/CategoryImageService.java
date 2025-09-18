package com.backend.product.service;

import java.util.concurrent.CompletableFuture;

import com.backend.common.dto.ImageMetaRes;

public interface CategoryImageService {
    void save(ImageMetaRes res, long cateogryId);

    CompletableFuture<String> delete(Long imageId);
}
