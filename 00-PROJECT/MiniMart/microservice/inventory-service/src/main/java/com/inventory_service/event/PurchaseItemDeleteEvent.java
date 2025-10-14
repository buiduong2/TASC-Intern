package com.inventory_service.event;

import com.common.event.DomainEvent;
import com.inventory_service.model.PurchaseItem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseItemDeleteEvent extends DomainEvent<PurchaseItem, Long> {

    public PurchaseItemDeleteEvent(PurchaseItem entity) {
        super(entity, entity.getId());
    }

}