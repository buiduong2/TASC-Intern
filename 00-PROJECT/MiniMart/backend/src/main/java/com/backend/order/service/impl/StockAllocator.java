package com.backend.order.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.backend.inventory.model.PurchaseItem;
import com.backend.inventory.model.StockAllocation;
import com.backend.inventory.model.StockAllocationStatus;
import com.backend.inventory.repository.PurchaseItemRepository;
import com.backend.inventory.repository.StockAllocationRepository;
import com.backend.order.exception.NotEnoughStockException;
import com.backend.order.model.Order;
import com.backend.order.model.OrderItem;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockAllocator {

    private final PurchaseItemRepository purchaseItemRepository;
    private final StockAllocationRepository stockAllocationRepository;

    @Transactional
    public void allocate(List<OrderItem> orderItems) {
        List<Long> productIds = orderItems.stream()
                .map(item -> item.getProduct().getId())
                .toList();

        List<PurchaseItem> purchaseItems = purchaseItemRepository.findAvaiableByProductIdInForUpdate(productIds);

        Map<Long, List<PurchaseItem>> mapPurchaseItemByProductId = purchaseItems.stream()
                .collect(Collectors.groupingBy(p -> p.getProduct().getId()));

        for (OrderItem orderItem : orderItems) {
            allocateForOrderItem(orderItem,
                    mapPurchaseItemByProductId.getOrDefault(orderItem.getProduct().getId(), List.of()));
        }

        purchaseItemRepository.saveAll(purchaseItems);
        stockAllocationRepository.saveAll(
                orderItems.stream().flatMap(i -> i.getAllocations().stream()).toList());
    }

    private void allocateForOrderItem(OrderItem orderItem, List<PurchaseItem> purchaseItems) {
        int remainingNeed = orderItem.getQuantity();
        List<StockAllocation> allocations = new ArrayList<>();
        orderItem.setAllocations(allocations);

        for (PurchaseItem purchaseItem : purchaseItems) {
            int available = purchaseItem.getRemainingQuantity();
            int allocated = Math.min(available, remainingNeed);

            if (allocated > 0) {
                purchaseItem.setRemainingQuantity(available - allocated);

                StockAllocation allocation = new StockAllocation();
                allocation.setOrderItem(orderItem);
                allocation.setPurchaseItem(purchaseItem);
                allocation.setAllocatedQuantity(allocated);

                allocations.add(allocation);
                remainingNeed -= allocated;
            }

            if (remainingNeed == 0)
                break;
        }

        if (remainingNeed > 0) {
            throw NotEnoughStockException.builder()
                    .message("A product not enough Stock")
                    .productId(orderItem.getProduct().getId())
                    .availableQuantity(orderItem.getQuantity() - remainingNeed)
                    .requestedQuantity(orderItem.getQuantity())
                    .build();
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void release(Order order) {
        List<StockAllocation> allocations = stockAllocationRepository.findByOrderId(order.getId(),
                StockAllocationStatus.ACTIVE);

        for (StockAllocation allocation : allocations) {
            int delta = allocation.getAllocatedQuantity();
            int updated = purchaseItemRepository.increaseRemainingQuantity(
                    allocation.getPurchaseItem().getId(), delta);
            allocation.setStatus(StockAllocationStatus.RELEASED);

            if (updated != 1) {
                throw new RuntimeException("Missing PurchaseItem when releasing stock");
            }
        }
        stockAllocationRepository.saveAll(allocations);
    }
}
