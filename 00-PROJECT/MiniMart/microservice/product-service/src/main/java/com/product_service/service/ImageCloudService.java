package com.product_service.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.multipart.MultipartFile;

import com.product_service.dto.res.ImageMetaRes;

public interface ImageCloudService {
    final String PRODUCT_PREFIX = "";

    final String CATEGORY_PREFIX = "";

    CompletableFuture<ImageMetaRes> updateImage(MultipartFile file, Long dto, String prefix);

    void deleteImageForId(String prefix, String id);
}
