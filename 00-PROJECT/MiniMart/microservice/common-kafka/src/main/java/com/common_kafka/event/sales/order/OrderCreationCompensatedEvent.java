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
public class OrderCreationCompensatedEvent extends AbstractSagaEvent {

    private LinkedHashSet<OrderItemData> items;

    private String reason;

    public OrderCreationCompensatedEvent(long orderId, long userId, LinkedHashSet<OrderItemData> items, String reason) {
        super(orderId, userId);
        this.reason = reason;
        this.items = items;
    }

}
