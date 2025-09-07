package com.backend.inventory.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.backend.inventory.dto.event.PurchaseCreatedEvent;
import com.backend.inventory.service.StockService;
import com.backend.product.dto.event.ProductCreatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockEventListener {

    private final StockService service;

    /**
     * 
     * Các lỗi xảy ra
     * Double Insert
     */

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotificationOnProductCreated(ProductCreatedEvent event) {
        service.create(event.getProductId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotificationOnPurchaseCreated(PurchaseCreatedEvent event) {
        service.increaseQuantity(event.getProductIds());
    }
}
