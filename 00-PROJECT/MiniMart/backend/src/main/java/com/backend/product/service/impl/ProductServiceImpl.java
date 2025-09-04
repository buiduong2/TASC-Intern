package com.backend.product.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.product.dto.res.ProductDTO;
import com.backend.product.dto.res.ProductDetailDTO;
import com.backend.product.mapper.ProductMapper;
import com.backend.product.model.Status;
import com.backend.product.repository.ProductRepository;
import com.backend.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    private final ProductMapper mapper;

    @Override
    public Page<ProductDTO> findByCategoryId(long categoryId, Pageable pageable) {
        return repository.findDTOByCategoryIdAndStatus(categoryId, Status.ACTIVE, pageable)
                .map(mapper::toDTO);
    }

    @Override
    public ProductDetailDTO findProductDetailById(long productId) {
        return repository.findDetailDTOByIdAndStatus(productId, Status.ACTIVE)
                .map(mapper::toDetailDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product ID = " + productId + " Not found"));
    }
}
