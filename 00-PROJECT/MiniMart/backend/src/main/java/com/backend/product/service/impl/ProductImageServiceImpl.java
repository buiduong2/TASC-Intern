package com.backend.product.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.dto.ImageMetaRes;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.product.model.Product;
import com.backend.product.model.ProductImage;
import com.backend.product.repository.ProductImageRepository;
import com.backend.product.repository.ProductRepository;
import com.backend.product.service.ProductImageService;

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
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product id= " + productId + "is not found for create Image"));
        ProductImage image;
        if (imageOpt.isPresent()) {
            image = imageOpt.get();
        } else {
            image = new ProductImage();
        }
        image.setAlt(product.getName());
        image.setProduct(product);
        image.setPublicId(res.getPublicId());
        image.setUrl(res.getUrl());
        repository.save(image);
    }

}
