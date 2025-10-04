package com.product_service.service.impl;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.product_service.dto.res.ImageMetaRes;
import com.product_service.service.ProductImageService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductImageServiceImpl implements ProductImageService {@Override
    public void save(ImageMetaRes res, long productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public CompletableFuture<String> delete(Long imageId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
