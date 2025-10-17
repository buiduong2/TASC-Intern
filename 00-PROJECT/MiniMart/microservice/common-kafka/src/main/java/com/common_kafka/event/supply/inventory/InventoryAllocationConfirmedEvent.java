package com.common_kafka.event.supply.inventory;

import java.util.Collection;

import com.common_kafka.event.shared.AbstractSagaEvent;
import com.common_kafka.event.shared.dto.AllocationItemSnapshot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryAllocationConfirmedEvent extends AbstractSagaEvent {
    private Long allocationId;
    private Collection<AllocationItemSnapshot> allocatedItems;

    public InventoryAllocationConfirmedEvent(
            long orderId,
            long userId,
            Long allocationId,
            Collection<AllocationItemSnapshot> allocatedItems) {

        super(orderId, userId);
        this.allocationId = allocationId;
        this.allocatedItems = allocatedItems;
    }

}
