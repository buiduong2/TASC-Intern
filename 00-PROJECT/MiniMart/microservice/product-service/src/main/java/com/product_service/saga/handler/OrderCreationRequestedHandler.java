package com.product_service.saga.handler;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.sales.order.OrderCreationRequestedEvent;
import com.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCreationRequestedHandler {
    private final ProductService productService;

    @KafkaListener(topics = KafkaTopics.SALES_ORDER_COMMAND, groupId = "catalog-validation-group")
    public void handleOrderCreationRequestedEvent(OrderCreationRequestedEvent event) {
        productService.processOrderCreationRequested(event);
    }
}
