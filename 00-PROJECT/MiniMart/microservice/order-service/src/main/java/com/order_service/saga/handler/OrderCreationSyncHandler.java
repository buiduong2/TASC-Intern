package com.order_service.saga.handler;

import org.springframework.data.util.Pair;

import com.order_service.model.Order;

public interface OrderCreationSyncHandler {
    Pair<Order, String> create(Order order);
}
