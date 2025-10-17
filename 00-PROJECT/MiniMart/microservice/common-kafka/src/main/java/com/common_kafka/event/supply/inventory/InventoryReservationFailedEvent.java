package com.common_kafka.event.supply.inventory;

import java.util.Set;

import com.common_kafka.event.shared.AbstractSagaEvent;
import com.common_kafka.event.shared.dto.FailedItemInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryReservationFailedEvent extends AbstractSagaEvent {
    private String reason;
    private Set<FailedItemInfo> failedItems;

    public InventoryReservationFailedEvent(long orderId, long userId, String reason, Set<FailedItemInfo> failedItems) {
        super(orderId, userId);
        this.reason = reason;
        this.failedItems = failedItems;
    }

}
