package com.inventory_service.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.inventory_service.model.Stock;
import com.inventory_service.repository.StockRepository;
import com.inventory_service.service.StockService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public void create(List<Long> productIds) {
        Set<Long> requiredIds = new HashSet<>(productIds);
        List<Long> eixstedProductIds = stockRepository.getProductIdByProductIdIn(productIds);

        requiredIds.removeAll(eixstedProductIds);
        if (requiredIds.isEmpty()) {
            return;
        }

        List<Stock> stocks = requiredIds.stream().map(productId -> {
            Stock stock = new Stock();
            stock.setProductId(productId);
            return stock;
        }).toList();
        stockRepository.saveAll(stocks);
    }

    @Override
    public void syncQuantity(long productId) {
        syncQuantity(List.of(productId));
    }

    @Override
    public void syncQuantity(List<Long> productIds) {
        stockRepository.syncQuantityByProductIdIn(productIds);
    }

}
