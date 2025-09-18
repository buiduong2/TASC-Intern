package com.backend.product.service.impl;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.dto.ImageMetaRes;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.utils.ErrorCode;
import com.backend.product.model.Category;
import com.backend.product.model.CategoryImage;
import com.backend.product.repository.CategoryImageRepository;
import com.backend.product.repository.CategoryRepository;
import com.backend.product.service.CategoryImageService;

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
                () -> new ResourceNotFoundException("Product id= " + productId + "is not found for create Image"));
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
        CategoryImage productImage = repository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND.format(imageId)));
        repository.delete(productImage);
        return CompletableFuture.completedFuture(productImage.getPublicId());
    }

}
