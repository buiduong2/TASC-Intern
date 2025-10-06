package com.product_service.service.impl;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.exception.GenericException;
import com.product_service.dto.res.ImageMetaRes;
import com.product_service.exception.ErrorCode;
import com.product_service.model.Category;
import com.product_service.model.CategoryImage;
import com.product_service.repository.CategoryImageRepository;
import com.product_service.repository.CategoryRepository;
import com.product_service.service.CategoryImageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class CategoryImageServiceImpl implements CategoryImageService {

    private final CategoryImageRepository repository;

    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public void save(ImageMetaRes res, long productId) {
        Optional<CategoryImage> imageOpt = repository.findByPublicId(res.getPublicId());
        Category product = categoryRepository.findById(productId).orElseThrow(
                () -> new GenericException(ErrorCode.PRODUCT_NOT_FOUND, productId));
        CategoryImage image;
        if (imageOpt.isPresent()) {
            image = imageOpt.get();
        } else {
            image = new CategoryImage();
        }
        image.setAlt(product.getName());
        product.setImage(image);
        image.setPublicId(res.getPublicId());
        image.setUrl(res.getUrl());
        repository.save(image);
    }

    @Override
    @Transactional
    @Async
    public CompletableFuture<String> delete(Long imageId) {
        if (imageId == null) {
            return null;
        }
        CategoryImage cateImage = repository.findById(imageId)
                .orElseThrow(() -> new GenericException(ErrorCode.CATEGORY_IMAGE_NOT_FOUND, imageId));
        repository.delete(cateImage);
        return CompletableFuture.completedFuture(cateImage.getPublicId());
    }

}
