package com.common_kafka.event.supply.inventory;

import com.common_kafka.event.shared.AbstractSagaEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryAllocationCompensateCompletedEvent extends AbstractSagaEvent {
    private Long allocationId;

    public InventoryAllocationCompensateCompletedEvent(
            long orderId,
            long userId,
            Long allocationId) {

        super(orderId, userId);
        this.allocationId = allocationId;

    }
}