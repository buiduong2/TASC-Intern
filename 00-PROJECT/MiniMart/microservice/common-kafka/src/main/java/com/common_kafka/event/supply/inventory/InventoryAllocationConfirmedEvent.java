package com.common_kafka.event.supply.inventory;

import java.util.Set;

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
    private Set<AllocationItemSnapshot> allocatedItems;

    public InventoryAllocationConfirmedEvent(long orderId, long userId, Long allocationId,
            Set<AllocationItemSnapshot> allocatedItems) {
        super(orderId, userId);
        this.allocationId = allocationId;
        this.allocatedItems = allocatedItems;
    }

}
