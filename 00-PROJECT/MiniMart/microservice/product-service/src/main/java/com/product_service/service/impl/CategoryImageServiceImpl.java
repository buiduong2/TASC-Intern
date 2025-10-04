package com.product_service.service.impl;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.product_service.dto.res.ImageMetaRes;
import com.product_service.service.CategoryImageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class CategoryImageServiceImpl implements CategoryImageService {

    @Override
    public void save(ImageMetaRes res, long cateogryId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public CompletableFuture<String> delete(Long imageId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
