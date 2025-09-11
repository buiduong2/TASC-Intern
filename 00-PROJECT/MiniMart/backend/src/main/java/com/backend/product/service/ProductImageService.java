package com.backend.product.service;

import com.backend.common.dto.ImageMetaRes;

public interface ProductImageService {
    void save(ImageMetaRes res, long productId);
}
