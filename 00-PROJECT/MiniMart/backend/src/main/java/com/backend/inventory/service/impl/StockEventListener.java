package com.backend.inventory.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.backend.inventory.dto.event.PurchaseCreatedEvent;
import com.backend.inventory.service.StockService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockEventListener {

    private final StockService service;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotificationOnPurchaseCreated(PurchaseCreatedEvent event) {
        service.updateOrCreateStock(event);
    }
}
