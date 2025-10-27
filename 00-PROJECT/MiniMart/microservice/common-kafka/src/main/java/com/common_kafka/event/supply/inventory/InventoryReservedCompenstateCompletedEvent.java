package com.common_kafka.event.supply.inventory;

import com.common_kafka.event.shared.AbstractSagaEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryReservedCompenstateCompletedEvent extends AbstractSagaEvent {

    private Long reservationId;

    public InventoryReservedCompenstateCompletedEvent(long orderId, long userId, Long reservationId) {
        super(orderId, userId);
        this.reservationId = reservationId;
    }
}
