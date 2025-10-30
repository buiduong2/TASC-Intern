package com.inventory_service.saga;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.sales.order.OrderCancellationRequestedEvent;
import com.common_kafka.event.sales.order.OrderStockAllocationRequestedEvent;
import com.common_kafka.event.shared.dto.AllocationItemSnapshot;
import com.common_kafka.event.supply.inventory.InventoryAllocationCompensateCompletedEvent;
import com.common_kafka.event.supply.inventory.InventoryAllocationConfirmedEvent;
import com.inventory_service.dto.res.StockAllocationResult;
import com.inventory_service.model.Allocation;
import com.inventory_service.model.AllocationItem;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AllocationSagaManager {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishInventoryAllocationConfirmedEvent(OrderStockAllocationRequestedEvent event,
            StockAllocationResult data) {
        Collection<AllocationItemSnapshot> allocationItemSnapshots = data.getAllocationItems().stream()
                .collect(Collectors.groupingBy(ai -> ai.getPurchaseItem().getProductId(),
                        Collectors.collectingAndThen(Collectors.toList(), ais -> {

                            BigDecimal sumWeightedCost = BigDecimal.ZERO;
                            int sumQuantity = 0;

                            for (AllocationItem item : ais) {
                                BigDecimal costPrice = item.getPurchaseItem().getCostPrice();
                                int quantity = item.getTotalAllocatedQuantity();

                                sumWeightedCost = sumWeightedCost.add(
                                        costPrice.multiply(new BigDecimal(quantity)));

                                sumQuantity += quantity;
                            }

                            BigDecimal avgCostPrice;
                            if (sumQuantity == 0) {
                                avgCostPrice = BigDecimal.ZERO;
                            } else {
                                avgCostPrice = sumWeightedCost.divide(
                                        new BigDecimal(sumQuantity),
                                        2,
                                        RoundingMode.HALF_UP);
                            }

                            long productId = ais.get(0).getPurchaseItem().getProductId();
                            return AllocationItemSnapshot.of(productId, sumQuantity, avgCostPrice);
                        })))
                .values();

        InventoryAllocationConfirmedEvent confirmEvent = new InventoryAllocationConfirmedEvent(
                event.getOrderId(),
                event.getUserId(),
                data.getAllocation().getId(),
                allocationItemSnapshots);

        kafkaTemplate.send(KafkaTopics.SUPPLY_INVENTORY_ALLOCATION_EVENTS, String.valueOf(event.getOrderId()), confirmEvent);

    }

    public void publishInventoryAllocationCompensateCompletedEvent(OrderCancellationRequestedEvent event,
            Allocation allocation) {
        InventoryAllocationCompensateCompletedEvent confirmEvent = new InventoryAllocationCompensateCompletedEvent(
                event.getOrderId(),
                event.getUserId(),
                allocation == null ? null : allocation.getId());

        kafkaTemplate.send(KafkaTopics.SUPPLY_INVENTORY_ALLOCATION_EVENTS, String.valueOf(event.getOrderId()), confirmEvent);

    }

}
