package com.product_service.service;

import java.util.concurrent.CompletableFuture;

import com.product_service.dto.res.ImageMetaRes;

public interface CategoryImageService {
    void save(ImageMetaRes res, long cateogryId);

    CompletableFuture<String> delete(Long imageId);
}
