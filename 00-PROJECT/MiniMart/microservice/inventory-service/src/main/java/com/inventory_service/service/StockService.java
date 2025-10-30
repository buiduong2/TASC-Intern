package com.inventory_service.service;

import java.util.List;

import com.common_kafka.event.sales.order.OrderCancellationRequestedEvent;
import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.common_kafka.event.sales.order.OrderStockReservationRequestedEvent;
import com.inventory_service.dto.res.ReservateStockResult;
import com.inventory_service.model.Allocation;

public interface StockService {
    void create(List<Long> productIds);

    void syncQuantity(long productId);

    void syncQuantity(List<Long> productIds);

    ReservateStockResult processOrderStockReservationRequested(OrderStockReservationRequestedEvent event);

    Allocation processOrderCreationCompensated(OrderCreationCompensatedEvent event);

    Allocation processOrderCancellationRequested(OrderCancellationRequestedEvent event);

}
