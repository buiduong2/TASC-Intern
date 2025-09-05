package com.backend.inventory.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.utils.EntityLookupHelper;
import com.backend.inventory.dto.event.PurchaseCreatedEvent;
import com.backend.inventory.dto.req.PurchaseItemReq;
import com.backend.inventory.model.Stock;
import com.backend.inventory.repository.StockRepository;
import com.backend.inventory.service.StockService;
import com.backend.product.model.Product;
import com.backend.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final ProductRepository productRepository;

    private final StockRepository repository;

    @Override
    @Transactional
    public void updateOrCreateStock(PurchaseCreatedEvent event) {

        List<PurchaseItemReq> reqItems = event.getItems();
        List<Long> productIds = reqItems.stream().map(PurchaseItemReq::getProductId).toList();

        Map<Long, Product> mapProductById = EntityLookupHelper.findMapByIdIn(productRepository, productIds, "Product");

        List<Stock> exsistedStock = repository.findByProductIdIn(productIds);
        Map<Long, Stock> mapStockByProductId = exsistedStock.stream()
                .collect(Collectors.toMap(s -> s.getProduct().getId(), Function.identity()));

        for (PurchaseItemReq reqItem : reqItems) {
            if (mapStockByProductId.containsKey(reqItem.getProductId())) {
                update(reqItem, mapStockByProductId.get(reqItem.getProductId()));
            } else {
                create(reqItem, mapProductById);
            }
        }

    }

    public void update(PurchaseItemReq req, Stock stock) {
        stock.setQuantity(stock.getQuantity() + req.getQuantity());
    }

    public void create(PurchaseItemReq req, Map<Long, Product> mapProductById) {
        Stock stock = new Stock();
        stock.setProduct(mapProductById.get(req.getProductId()));
        stock.setQuantity(req.getQuantity());

        repository.save(stock);
    }

}
