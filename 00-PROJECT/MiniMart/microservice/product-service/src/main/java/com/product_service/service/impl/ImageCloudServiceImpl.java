package com.product_service.service.impl;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.product_service.dto.res.ImageMetaRes;
import com.product_service.service.ImageCloudService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageCloudServiceImpl implements ImageCloudService {

    @Override
    public CompletableFuture<ImageMetaRes> updateImage(MultipartFile file, Long dto, String prefix) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateImage'");
    }

    @Override
    public void deleteImageForId(String prefix, String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteImageForId'");
    }

}
