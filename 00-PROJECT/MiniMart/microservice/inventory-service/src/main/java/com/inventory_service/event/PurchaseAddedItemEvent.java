package com.inventory_service.event;

import com.common.event.DomainEvent;
import com.inventory_service.model.Purchase;
import com.inventory_service.model.PurchaseItem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseAddedItemEvent extends DomainEvent<Purchase, Long> {

    private final PurchaseItem newPurchaseItem;

    public PurchaseAddedItemEvent(Purchase entity, PurchaseItem purchaseItem) {
        super(entity, entity.getId());
        this.newPurchaseItem = purchaseItem;
    }

}
