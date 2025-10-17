package com.common_kafka.event.sales.order;

import com.common_kafka.event.shared.AbstractSagaEvent;

public class OrderStockAllocationRequestedEvent extends AbstractSagaEvent {

    public OrderStockAllocationRequestedEvent(long orderId, long userId) {
        super(orderId, userId);
    }

}
