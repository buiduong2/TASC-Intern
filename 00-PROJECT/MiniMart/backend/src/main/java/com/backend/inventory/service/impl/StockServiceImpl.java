package com.backend.inventory.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.inventory.model.Stock;
import com.backend.inventory.repository.StockRepository;
import com.backend.inventory.service.StockService;
import com.backend.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final ProductRepository productRepository;

    private final StockRepository repository;

    @Override
    @Transactional
    public void syncQuantity(List<Long> productIds) {

        for (Long productId : productIds) {
            int result = repository.synckQuantity(productId);
            if (result != 1) {
                throw new IllegalArgumentException("Stock not found for ProductID = " + productId);
            }
        }
    }

    @Transactional
    @Override
    public void create(long productId) {
        Stock stock = new Stock();
        stock.setProduct(productRepository.getReferenceById(productId));
        repository.save(stock);
    }

}
