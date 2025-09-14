package com.backend.inventory.service.impl;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.backend.inventory.dto.event.PurchaseCreatedEvent;
import com.backend.inventory.dto.event.PurchaseItemUpdateEvent;
import com.backend.inventory.service.StockService;
import com.backend.order.dto.event.OrderCreatedEvent;
import com.backend.product.dto.event.ProductCreatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockEventListener {

    private final StockService service;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotificationOnProductCreated(ProductCreatedEvent event) {
        service.create(event.getProductId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotificationOnPurchaseCreated(PurchaseCreatedEvent event) {
        service.syncQuantity(event.getProductIds());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotifiycationOnOrderCreated(OrderCreatedEvent event) {
        service.syncQuantity(event.getOrderItems().stream().map(i -> i.getProductId()).toList());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotificationOnPurchaseItemUpdate(PurchaseItemUpdateEvent event) {
        service.syncQuantity(List.of(event.getProductId()));
    }
}
