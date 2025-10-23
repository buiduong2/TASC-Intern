package com.inventory_service.service;

import java.util.List;

import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.common_kafka.event.shared.res.SagaResult;
import com.inventory_service.dto.res.ReservateStockResult;

public interface StockService {
    void create(List<Long> productIds);

    void syncQuantity(long productId);

    void syncQuantity(List<Long> productIds);

    SagaResult<ReservateStockResult> processProductValidationEvent(ProductValidationPassedEvent event);

    void processOrderCreationCompensated(OrderCreationCompensatedEvent event);

}
