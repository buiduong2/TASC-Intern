package com.inventory_service.event;

import com.common.event.DomainEvent;
import com.inventory_service.model.PurchaseItem;

public class PurchaseItemUpdateEvent extends DomainEvent<PurchaseItem, Long> {

    public PurchaseItemUpdateEvent(PurchaseItem entity) {
        super(entity, entity.getId());
    }

}
