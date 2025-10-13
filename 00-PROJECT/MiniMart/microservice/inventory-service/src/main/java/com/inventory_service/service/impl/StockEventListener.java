package com.inventory_service.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.inventory_service.event.OrderCompletedEvent;
import com.inventory_service.event.PurchaseActiveEvent;
import com.inventory_service.event.PurchaseArchivedEvent;
import com.inventory_service.event.PurchaseAddedItemEvent;
import com.inventory_service.model.Purchase;
import com.inventory_service.model.PurchaseItem;
import com.inventory_service.model.Stock;
import com.inventory_service.repository.StockRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockEventListener {

    private final StockRepository stockRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePurchaseConfirmed(PurchaseActiveEvent event) {
        syncQunatity(toProductIds(event.getEntity()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePurchaseArchive(PurchaseArchivedEvent event) {
        syncQunatity(toProductIds(event.getEntity()));
    }

    @EventListener
    public void handleOrderCompleted(OrderCompletedEvent event) {

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePurchaseItemAdded(PurchaseAddedItemEvent event) {
        syncQunatity(List.of(event.getNewPurchaseItem().getProductId()));
    }

    private void syncQunatity(List<Long> productIds) {
        insertIfNotExists(productIds);
        stockRepository.syncQuantityByProductIdIn(productIds);
    }

    private List<Long> toProductIds(Purchase purchase) {
        return purchase.getPurchaseItems().stream().map(PurchaseItem::getProductId).toList();
    }

    private void insertIfNotExists(List<Long> productIds) {
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

}
