package com.inventory_service.event;

import com.common.event.DomainEvent;
import com.inventory_service.model.Purchase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseArchivedEvent extends DomainEvent<Purchase, Long> {

    public PurchaseArchivedEvent(Purchase entity) {
        super(entity, entity.getId());
    }

}
