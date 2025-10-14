package com.inventory_service.service.impl;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.common.event.DomainEvent;
import com.inventory_service.enums.PurchaseStatus;
import com.inventory_service.event.OrderCompletedEvent;
import com.inventory_service.event.PurchaseActiveEvent;
import com.inventory_service.event.PurchaseAddedItemEvent;
import com.inventory_service.event.PurchaseArchivedEvent;
import com.inventory_service.event.PurchaseItemDeleteEvent;
import com.inventory_service.event.PurchaseItemUpdateEvent;
import com.inventory_service.model.Purchase;
import com.inventory_service.model.PurchaseItem;
import com.inventory_service.service.StockService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockEventListener {

    private final StockService stockService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePurchaseConfirmed(PurchaseActiveEvent event) {
        stockService.syncQuantity(toProductIds(event));
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handlePurchaseArchive(PurchaseArchivedEvent event) {
        stockService.syncQuantity(toProductIds(event));
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handlePurchaseItemUpdate(PurchaseItemUpdateEvent event) {
        stockService.syncQuantity(event.getEntity().getProductId());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handlePurchaseItemDelete(PurchaseItemDeleteEvent event) {
        stockService.syncQuantity(event.getEntity().getProductId());
    }

    @EventListener
    public void handleOrderCompleted(OrderCompletedEvent event) {

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePurchaseItemAdded(PurchaseAddedItemEvent event) {
        if (event.getEntity().getStatus() == PurchaseStatus.DRAFT) {
            return;
        } else {
            throw new RuntimeException("System may not allow add item on Purchase when status does not equal DRAFT");
        }

    }

    private List<Long> toProductIds(DomainEvent<Purchase, Long> event) {
        return event.getEntity().getPurchaseItems().stream().map(PurchaseItem::getProductId).toList();
    }

}
