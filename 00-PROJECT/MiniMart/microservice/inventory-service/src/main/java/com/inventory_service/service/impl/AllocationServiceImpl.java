package com.inventory_service.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.common_kafka.event.sales.order.OrderStockAllocationRequestedEvent;
import com.common_kafka.event.shared.dto.OrderItemData;
import com.common_kafka.event.shared.helper.SagaResultUtils;
import com.common_kafka.event.shared.res.SagaResult;
import com.inventory_service.dto.req.StockAllocationFilter;
import com.inventory_service.dto.res.StockAllocationResult;
import com.inventory_service.dto.res.StockAllocationSummaryDTO;
import com.inventory_service.enums.AllocationStatus;
import com.inventory_service.enums.OrderReservationLogStatus;
import com.inventory_service.enums.PurchaseStatus;
import com.inventory_service.exception.AllocationNotEnoughStockException;
import com.inventory_service.exception.AllocationNotFoundException;
import com.inventory_service.model.Allocation;
import com.inventory_service.model.AllocationItem;
import com.inventory_service.model.OrderReservationLog;
import com.inventory_service.model.PurchaseItem;
import com.inventory_service.repository.AllocationItemRepository;
import com.inventory_service.repository.AllocationRepository;
import com.inventory_service.repository.OrderReservationLogRepository;
import com.inventory_service.repository.PurchaseItemRepository;
import com.inventory_service.service.AllocationService;
import com.inventory_service.service.StockTransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AllocationServiceImpl implements AllocationService {

    private final AllocationRepository repository;

    private final PurchaseItemRepository purchaseItemRepository;

    private final OrderReservationLogRepository orderReservationLogRepository;

    private final StockTransactionService stockTransactionService;

    private final AllocationItemRepository allocationItemRepository;

    @Override
    public Page<StockAllocationSummaryDTO> findAll(StockAllocationFilter filter, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Transactional
    @Override
    public SagaResult<StockAllocationResult> processOrderStockAllocationRequest(
            OrderStockAllocationRequestedEvent event) {
        return SagaResultUtils.execute(() -> {
            long orderId = event.getOrderId();

            Allocation allocation = repository.findByOrderIdAndStatusForUpdate(orderId, AllocationStatus.RESERVE)
                    .orElseThrow(() -> new AllocationNotFoundException(
                            "Allocation Not found when processOrderStockAllocationRequest for orderId = " + orderId));

            List<Long> productIds = event.getItems().stream().map(OrderItemData::getProductId).toList();

            List<PurchaseItem> pis = purchaseItemRepository.findByProductIdInAndPurchaseStatusForAllocation(
                    productIds,
                    PurchaseStatus.ACTIVE);

            List<OrderReservationLog> logs = orderReservationLogRepository.findByOrderIdAndStatusForAllocation(
                    orderId,
                    OrderReservationLogStatus.RESERVED);

            // Process
            List<AllocationItem> allocationItems = allocationForOrder(pis, logs);
            commitAllocationForOrder(logs);

            // Update Allocation
            allocation.addAllocationItems(allocationItems);
            allocation.setStatus(AllocationStatus.ALLOCATED);
            repository.save(allocation);

            purchaseItemRepository.saveAll(pis);
            orderReservationLogRepository.saveAll(logs);

            return StockAllocationResult.of(allocation, allocationItems);
        });
    }

    private void commitAllocationForOrder(List<OrderReservationLog> logs) {
        for (OrderReservationLog log : logs) {
            stockTransactionService.commitSingleProduct(log);
            log.setStatus(OrderReservationLogStatus.COMMITTED);
        }
    }

    private List<AllocationItem> allocationForOrder(
            List<PurchaseItem> purchaseItems,
            List<OrderReservationLog> logs) {

        Map<Long, List<PurchaseItem>> mapPurchaseItemByProductId = purchaseItems
                .stream()
                .collect(Collectors.groupingBy(p -> p.getProductId()));

        List<AllocationItem> newAllocationItems = new ArrayList<>();

        for (OrderReservationLog log : logs) {
            List<PurchaseItem> purchaseItemsForProductId = mapPurchaseItemByProductId.getOrDefault(
                    log.getProductId(),
                    Collections.emptyList());
            List<AllocationItem> ais = allocateForOrderReservationLog(log, purchaseItemsForProductId);
            newAllocationItems.addAll(ais);
        }
        return newAllocationItems;
    }

    private List<AllocationItem> allocateForOrderReservationLog(
            OrderReservationLog log,
            List<PurchaseItem> purchaseItems) {

        int remainingNeed = log.getQuantityReserved();

        List<AllocationItem> allocationItems = new ArrayList<>();

        for (PurchaseItem purchaseItem : purchaseItems) {
            int available = purchaseItem.getRemainingQuantity();
            int allocated = Math.min(available, remainingNeed);

            if (allocated > 0) {
                purchaseItem.setRemainingQuantity(available - allocated);

                AllocationItem allocation = new AllocationItem();
                allocation.setOrderItemId(log.getOrderItemId());
                allocation.setPurchaseItem(purchaseItem);
                allocation.setTotalAllocatedQuantity(allocated);

                remainingNeed -= allocated;

                allocationItems.add(allocation);
            }

            if (remainingNeed == 0)
                break;

        }

        if (remainingNeed > 0) {
            throw AllocationNotEnoughStockException.builder()
                    .message("A product not enough Stock")
                    .productId(log.getProductId())
                    .availableQuantity(log.getQuantityReserved() - remainingNeed)
                    .requestedQuantity(log.getQuantityReserved())
                    .build();
        }

        return allocationItems;
    }

    /**
     * Method sẽ tiến hành đảo ngược quá trình AllocationItem
     */

    @Transactional
    @Override
    public Allocation processOrderCreationCompensated(OrderCreationCompensatedEvent event) {
        Optional<Allocation> opt = repository
                .findByOrderIdAndStatusForCompenstate(event.getOrderId(), AllocationStatus.ALLOCATED);
        if (opt.isEmpty()) {
            return null;
        }

        Allocation allocation = opt.get();

        List<AllocationItem> allocationItems = allocationItemRepository
                .findByAllocationIdForCompensate(allocation.getId())
                .stream()
                .sorted(Comparator.comparingLong(ai -> ai.getPurchaseItem().getProductId()))
                .toList();

        for (AllocationItem allocationItem : allocationItems) {
            PurchaseItem pi = allocationItem.getPurchaseItem();
            stockTransactionService.compensateSingleCommitProduct(event.getOrderId(), pi.getProductId());
        }

        allocation.setStatus(AllocationStatus.RELEASED);
        repository.save(allocation);

        return allocation;
    }

}
