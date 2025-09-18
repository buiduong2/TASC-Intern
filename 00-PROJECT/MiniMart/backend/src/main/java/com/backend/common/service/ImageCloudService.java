package com.backend.common.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.multipart.MultipartFile;

import com.backend.common.dto.ImageMetaRes;

public interface ImageCloudService {
    CompletableFuture<ImageMetaRes> updateImage(MultipartFile file, Long dto, String prefix);

    void deleteImageForId(String prefix, String id);

}
