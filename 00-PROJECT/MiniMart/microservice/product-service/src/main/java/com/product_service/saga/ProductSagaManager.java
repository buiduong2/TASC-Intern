package com.product_service.saga;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.catalog.product.ProductValidationFailedEvent;
import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.event.sales.order.OrderProductValidationRequestedEvent;
import com.common_kafka.event.shared.dto.ValidatedItemSnapshot;
import com.product_service.model.Product;
import com.product_service.saga.utils.ProductSagaUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductSagaManager {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ProductSagaUtils utils;

    public void publishProductValidationPassedEvent(OrderProductValidationRequestedEvent event,
            List<Product> products) {

        Set<ValidatedItemSnapshot> validatedItemSnapshots = utils.getValidatedItemSnapshots(event, products);

        ProductValidationPassedEvent passedEvent = new ProductValidationPassedEvent(
                event.getOrderId(),
                event.getUserId(),
                validatedItemSnapshots);

        kafkaTemplate.send(
                KafkaTopics.CATALOG_PRODUCT_VALIDATION_EVENTS,
                String.valueOf(event.getOrderId()),
                passedEvent);
    }

    public void publishProductValidationFailedEvent(OrderProductValidationRequestedEvent event,
            List<Product> products) {

        Set<Long> failedProdutIds = utils.get(event, products);

        ProductValidationFailedEvent failedEvent = new ProductValidationFailedEvent(
                event.getOrderId(),
                event.getUserId(),
                "Some product is missing",
                failedProdutIds);

        kafkaTemplate.send(
                KafkaTopics.CATALOG_PRODUCT_VALIDATION_EVENTS,
                String.valueOf(event.getOrderId()),
                failedEvent);
    }

    public void publishProductValidationFailedEvent(OrderProductValidationRequestedEvent event) {
        ProductValidationFailedEvent failedEvent = new ProductValidationFailedEvent(
                event.getOrderId(),
                event.getUserId(),
                "Product Service is Error",
                Collections.emptySet());

        kafkaTemplate.send(
                KafkaTopics.CATALOG_PRODUCT_VALIDATION_EVENTS,
                String.valueOf(event.getOrderId()),
                failedEvent);
    }

}
