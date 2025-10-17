package com.product_service.saga;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.common.utils.Utils;
import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.catalog.product.ProductValidationFailedEvent;
import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.event.sales.order.OrderCreationRequestedEvent;
import com.common_kafka.event.shared.dto.OrderItemData;
import com.common_kafka.event.shared.dto.ValidatedItemSnapshot;
import com.product_service.model.Product;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductSagaManager {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishProductValidationPassedEvent(OrderCreationRequestedEvent event, List<Product> products) {
        Map<Long, Integer> mapQuantityByProductId = event.getItems()
                .stream()
                .collect(Collectors.toMap(OrderItemData::getProductId, OrderItemData::getQuantity));

        ProductValidationPassedEvent passedEvent = new ProductValidationPassedEvent(
                event.getOrderId(),
                event.getUserId(),
                products.stream()
                        .map(p -> new ValidatedItemSnapshot(
                                p.getId(),
                                mapQuantityByProductId.get(p.getId()),
                                Utils.coalesce(p.getSalePrice(), p.getCompareAtPrice())))
                        .collect(Collectors.toSet()));

        kafkaTemplate.send(
                KafkaTopics.CATALOG_PRODUCT_EVENTS,
                String.valueOf(event.getOrderId()),
                passedEvent);
    }

    public void publishProductValidationFailedEvent(OrderCreationRequestedEvent event, List<Product> products) {
        Set<Long> existedIds = products.stream().map(Product::getId).collect(Collectors.toSet());
        Set<Long> failedProdutIds = event.getItems()
                .stream()
                .map(OrderItemData::getProductId)
                .filter(id -> !existedIds.contains(id))
                .collect(Collectors.toSet());

        ProductValidationFailedEvent failedEvent = new ProductValidationFailedEvent(
                event.getOrderId(),
                event.getUserId(),
                "Some product is missing",
                failedProdutIds);

        kafkaTemplate.send(
                KafkaTopics.CATALOG_PRODUCT_EVENTS,
                String.valueOf(event.getOrderId()),
                failedEvent);
    }

}
