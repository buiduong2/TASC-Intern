package com.inventory_service.saga;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.event.sales.order.OrderCancellationRequestedEvent;
import com.common_kafka.event.shared.dto.FailedItemInfo;
import com.common_kafka.event.shared.dto.ReservedItemSnapshot;
import com.common_kafka.event.shared.dto.ValidatedItemSnapshot;
import com.common_kafka.event.supply.inventory.InventoryReservationFailedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedCompenstateCompletedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedConfirmedEvent;
import com.inventory_service.model.Allocation;
import com.inventory_service.model.OrderReservationLog;
import com.inventory_service.model.Stock;
import com.inventory_service.repository.StockRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockSagaManager {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final StockRepository stockRepository;

    public void publishInventoryReservedConfirmedEvent(
            ProductValidationPassedEvent event,
            List<OrderReservationLog> logs,
            Allocation allocation) {

        Set<ReservedItemSnapshot> reservedItems = logs.stream()
                .map(l -> new ReservedItemSnapshot(l.getProductId(), l.getQuantityReserved()))
                .collect(Collectors.toSet());

        InventoryReservedConfirmedEvent confirmEvent = new InventoryReservedConfirmedEvent(
                event.getOrderId(),
                event.getUserId(),
                reservedItems,
                allocation.getId());

        kafkaTemplate.send(
                KafkaTopics.SUPPLY_INVENTORY_RESERVATION,
                String.valueOf(event.getOrderId()),
                confirmEvent);

    }

    public void publishInventoryReservedFailedEvent(ProductValidationPassedEvent event, String reason) {

        Set<FailedItemInfo> failedItems = new HashSet<>();

        List<Stock> stocks = stockRepository.findByProductIdIn(
                event.getValidatedItems()
                        .stream()
                        .map(ValidatedItemSnapshot::getProductId)
                        .toList());
        Map<Long, Integer> mapAvaiableQtyByProductId = stocks.stream()
                .collect(Collectors.toMap(Stock::getProductId, s -> s.getAvaiableQuantity()));

        for (ValidatedItemSnapshot vi : event.getValidatedItems()) {
            Integer avaiableQty = mapAvaiableQtyByProductId.get(vi.getProductId());
            if (avaiableQty != null && vi.getQuantity() > avaiableQty) {
                failedItems.add(
                        new FailedItemInfo(
                                vi.getProductId(),
                                vi.getQuantity(),
                                avaiableQty));
            }

        }

        InventoryReservationFailedEvent failedEvent = new InventoryReservationFailedEvent(
                event.getOrderId(),
                event.getUserId(),
                reason,
                failedItems);

        kafkaTemplate.send(
                KafkaTopics.SUPPLY_INVENTORY_RESERVATION,
                String.valueOf(event.getOrderId()),
                failedEvent);

    }

    public void publishInventoryReservedCompenstateCompletedEvent(
            OrderCancellationRequestedEvent event,
            Allocation allocation) {

        InventoryReservedCompenstateCompletedEvent confirmEvent = new InventoryReservedCompenstateCompletedEvent(
                event.getOrderId(),
                event.getUserId(),
                allocation == null ? null : allocation.getId());

        kafkaTemplate.send(
                KafkaTopics.SUPPLY_INVENTORY_RESERVATION,
                String.valueOf(event.getOrderId()),
                confirmEvent);
    }
}
