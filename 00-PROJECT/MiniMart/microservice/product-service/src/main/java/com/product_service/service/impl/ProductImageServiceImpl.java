package com.product_service.service.impl;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.exception.GenericException;
import com.product_service.dto.res.ImageMetaRes;
import com.product_service.exception.ErrorCode;
import com.product_service.model.Product;
import com.product_service.model.ProductImage;
import com.product_service.repository.ProductImageRepository;
import com.product_service.repository.ProductRepository;
import com.product_service.service.ProductImageService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository repository;

    private final ProductRepository productRepository;

    @Transactional
    @Override
    public void save(ImageMetaRes res, long productId) {
        Optional<ProductImage> imageOpt = repository.findByPublicId(res.getPublicId());
        Product product = productRepository.findById(productId)
                .orElseThrow(
                        () -> new GenericException(ErrorCode.PRODUCT_NOT_FOUND, productId));
        ProductImage image;
        if (imageOpt.isPresent()) {
            image = imageOpt.get();
        } else {
            image = new ProductImage();
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
        ProductImage productImage = repository.findById(imageId)
                .orElseThrow(() -> new GenericException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND, imageId));
        repository.delete(productImage);
        return CompletableFuture.completedFuture(productImage.getPublicId());
    }

}
