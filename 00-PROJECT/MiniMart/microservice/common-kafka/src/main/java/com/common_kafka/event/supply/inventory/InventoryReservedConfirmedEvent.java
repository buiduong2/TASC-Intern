package com.common_kafka.event.supply.inventory;

import java.util.Set;

import com.common_kafka.event.shared.AbstractSagaEvent;
import com.common_kafka.event.shared.dto.ReservedItemSnapshot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryReservedConfirmedEvent extends AbstractSagaEvent {

    private Set<ReservedItemSnapshot> reservedItems;
    private Long reservationId;

    public InventoryReservedConfirmedEvent(long orderId, long userId, Set<ReservedItemSnapshot> reservedItems,
            Long reservationId) {
        super(orderId, userId);
        this.reservedItems = reservedItems;
        this.reservationId = reservationId;
    }

}
