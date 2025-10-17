package com.product_service.saga;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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
        Map<Long, OrderItemData> mapByProductId = event.getItems()
                .stream()
                .collect(Collectors.toMap(OrderItemData::getProductId, Function.identity()));

        ProductValidationPassedEvent passedEvent = new ProductValidationPassedEvent(
                event.getOrderId(),
                event.getUserId(),
                products.stream()
                        .map(p -> {
                            OrderItemData orderItemData = mapByProductId.get(p.getId());
                            long orderItemId = orderItemData.getOrderItemId();
                            int quantity = orderItemData.getQuantity();
                            return new ValidatedItemSnapshot(
                                    orderItemId,
                                    p.getId(),
                                    quantity,
                                    Utils.coalesce(p.getSalePrice(), p.getCompareAtPrice()));
                        })
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
