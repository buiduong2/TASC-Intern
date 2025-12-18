package com.product_service.saga.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.common.utils.Utils;
import com.common_kafka.event.sales.order.OrderProductValidationRequestedEvent;
import com.common_kafka.event.shared.dto.OrderItemData;
import com.common_kafka.event.shared.dto.ValidatedItemSnapshot;
import com.product_service.model.Product;

@Component
public class ProductSagaUtils {

    public Set<ValidatedItemSnapshot> getValidatedItemSnapshots(OrderProductValidationRequestedEvent event,
            List<Product> products) {
        Map<Long, OrderItemData> mapByProductId = event.getItems()
                .stream()
                .collect(Collectors.toMap(OrderItemData::getProductId, Function.identity()));

        return products.stream()
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
                .collect(Collectors.toSet());
    }

    public Set<Long> get(OrderProductValidationRequestedEvent event, List<Product> products) {
        Set<Long> existedIds = products.stream().map(Product::getId).collect(Collectors.toSet());
        Set<Long> failedProdutIds = event.getItems()
                .stream()
                .map(OrderItemData::getProductId)
                .filter(id -> !existedIds.contains(id))
                .collect(Collectors.toSet());

        return failedProdutIds;
    }
}
