package com.common_kafka.event.sales.order;

import java.util.Set;

import com.common_kafka.event.shared.AbstractSagaEvent;
import com.common_kafka.event.shared.dto.OrderItemData;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderStockReservationRequestedEvent extends AbstractSagaEvent {
    private Set<OrderItemData> items;

    public OrderStockReservationRequestedEvent(long orderId, long userId, Set<OrderItemData> items) {
        super(orderId, userId);
        this.items = items;
    }
}
