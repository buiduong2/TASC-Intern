package com.inventory_service.event;

import java.util.List;

import com.common.event.DomainEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseDeletedEvent extends DomainEvent<Object, Long> {

    private final List<Long> purchaseItemIds;

    public PurchaseDeletedEvent(Long entityId, List<Long> purchaseItemIds) {
        super(null, entityId);
        this.purchaseItemIds = purchaseItemIds;
    }

}
