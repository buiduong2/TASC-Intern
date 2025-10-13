package com.inventory_service.event;

import com.common.event.DomainEvent;
import com.inventory_service.model.Purchase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseActiveEvent extends DomainEvent<Purchase, Long> {

    public PurchaseActiveEvent(Purchase entity) {
        super(entity, entity.getId());
    }

}
