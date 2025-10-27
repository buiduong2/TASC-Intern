package com.common_kafka.event.sales.order;

import java.util.LinkedHashSet;

import com.common_kafka.event.shared.AbstractSagaEvent;
import com.common_kafka.event.shared.dto.OrderItemData;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderCancellationRequestedEvent extends AbstractSagaEvent {

    private LinkedHashSet<OrderItemData> items;

    public OrderCancellationRequestedEvent(long orderId, long userId, LinkedHashSet<OrderItemData> items) {
        super(orderId, userId);
        this.items = items;
    }

}
