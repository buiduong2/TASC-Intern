package com.backend.product.service.impl;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.dto.ImageMetaRes;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.model.BaseImage;
import com.backend.common.utils.ErrorCode;
import com.backend.product.model.Product;
import com.backend.product.model.ProductImage;
import com.backend.product.repository.ProductImageRepository;
import com.backend.product.repository.ProductRepository;
import com.backend.product.service.ProductImageService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductImageServiceImpl<T extends BaseImage> implements ProductImageService {

    private final ProductImageRepository repository;

    private final ProductRepository productRepository;

    @Transactional
    @Override
    public void save(ImageMetaRes res, long productId) {
        Optional<ProductImage> imageOpt = repository.findByPublicId(res.getPublicId());
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product id= " + productId + "is not found for create Image"));
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
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND.format(imageId)));
        repository.delete(productImage);
        return CompletableFuture.completedFuture(productImage.getPublicId());
    }

}
