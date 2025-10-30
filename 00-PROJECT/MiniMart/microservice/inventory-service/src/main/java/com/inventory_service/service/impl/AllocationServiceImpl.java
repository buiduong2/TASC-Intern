package com.inventory_service.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.common_kafka.event.sales.order.OrderCancellationRequestedEvent;
import com.common_kafka.event.sales.order.OrderStockAllocationRequestedEvent;
import com.common_kafka.event.shared.dto.OrderItemData;
import com.common_kafka.exception.saga.BusinessInvariantViolationException;
import com.common_kafka.exception.saga.IdempotentEventException;
import com.common_kafka.exception.saga.InvalidStateException;
import com.common_kafka.exception.saga.ResourceNotFoundException;
import com.inventory_service.dto.req.StockAllocationFilter;
import com.inventory_service.dto.res.StockAllocationResult;
import com.inventory_service.dto.res.StockAllocationSummaryDTO;
import com.inventory_service.enums.AllocationStatus;
import com.inventory_service.enums.OrderReservationLogStatus;
import com.inventory_service.enums.PurchaseStatus;
import com.inventory_service.exception.AllocationNotEnoughStockException;
import com.inventory_service.exception.StockReservationException;
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

    /**
     * ├─ Tìm Allocation (pessimistic lock)
     * ├─ Validate state = RESERVE
     * ├─ Lấy các PurchaseItem khả dụng
     * ├─ Lấy danh sách OrderReservationLog
     * ├─ Dò FIFO để phân bổ hàng
     * ├─ Commit từng sản phẩm vào StockTransaction
     * ├─ Update Allocation → ALLOCATED
     * ├─ Save tất cả
     */
    @Transactional
    @Override
    public StockAllocationResult processOrderStockAllocationRequest(
            OrderStockAllocationRequestedEvent event) {

        long orderId = event.getOrderId();

        Allocation allocation = repository.findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("Allocation", orderId));

        if (allocation.getStatus() != AllocationStatus.RESERVE) {
            throw InvalidStateException.of(
                    "Allocation",
                    allocation.getId(),
                    allocation.getStatus().name(),
                    AllocationStatus.RESERVE.name());
        }

        if (allocation.getAllocationItems() != null && !allocation.getAllocationItems().isEmpty()) {
            throw new IdempotentEventException("Allocation", allocation.getId(), Instant.now());
        }

        List<Long> productIds = event.getItems().stream().map(OrderItemData::getProductId).toList();

        List<PurchaseItem> pis = purchaseItemRepository.findByProductIdInAndPurchaseStatusForAllocation(
                productIds,
                PurchaseStatus.ACTIVE);

        List<OrderReservationLog> logs = orderReservationLogRepository.findByOrderIdAndStatusForAllocation(
                orderId,
                OrderReservationLogStatus.RESERVED);

        if (logs.size() == 0) {
            throw new BusinessInvariantViolationException(
                    "OrderReservationLog",
                    orderId,
                    "Some thing wrong we don't have any OrderReservationLog");
        }

        try {
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

        } catch (StockReservationException e) {
            throw BusinessInvariantViolationException.of("Allocation", allocation.getId(), e.getMessage());
        } catch (AllocationNotEnoughStockException e) {
            throw BusinessInvariantViolationException.of("AllocationItem", e.getProductId(), e.getMessage());
        }
    }

    private void commitAllocationForOrder(List<OrderReservationLog> logs) {
        for (OrderReservationLog log : logs) {
            stockTransactionService.commitSingleProduct(log);
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(maxAttempts = 3)
    @Override
    public Allocation processOrderCancellationRequested(OrderCancellationRequestedEvent event) {
        Optional<Allocation> opt = repository
                .findByOrderIdAndStatusForCompenstate(event.getOrderId(), AllocationStatus.ALLOCATED);
        if (opt.isEmpty()) {
            Allocation dummy = new Allocation();
            dummy.setOrderId(event.getOrderId());
            dummy.setUserId(event.getUserId());
            dummy.setStatus(AllocationStatus.RELEASED);
            repository.saveAndFlush(dummy);
            return dummy;
        }

        Allocation allocation = opt.get();

        if (allocation.getStatus() == AllocationStatus.RELEASED) {
            throw new IdempotentEventException("Allocation", allocation.getId(), Instant.now());
        }

        if (allocation.getStatus() != AllocationStatus.ALLOCATED) {
            return allocation;
        }

        

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
