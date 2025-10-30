package com.inventory_service.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.common_kafka.event.sales.order.OrderCancellationRequestedEvent;
import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.common_kafka.event.sales.order.OrderStockReservationRequestedEvent;
import com.common_kafka.event.shared.dto.OrderItemData;
import com.common_kafka.event.shared.dto.ValidatedItemSnapshot;
import com.common_kafka.exception.saga.IdempotentEventException;
import com.common_kafka.exception.saga.InvalidStateException;
import com.common_kafka.exception.saga.LogicViolationException;
import com.common_kafka.exception.saga.ResourceNotFoundException;
import com.inventory_service.dto.res.ReservateStockResult;
import com.inventory_service.enums.AllocationStatus;
import com.inventory_service.exception.StockReservationException;
import com.inventory_service.model.Allocation;
import com.inventory_service.model.OrderReservationLog;
import com.inventory_service.model.Stock;
import com.inventory_service.repository.AllocationRepository;
import com.inventory_service.repository.StockRepository;
import com.inventory_service.service.StockService;
import com.inventory_service.service.StockTransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository service;

    private final StockTransactionService transactionService;

    private final AllocationRepository allocationRepository;

    @Override
    public void create(List<Long> productIds) {
        Set<Long> requiredIds = new HashSet<>(productIds);
        List<Long> eixstedProductIds = service.getProductIdByProductIdIn(productIds);

        requiredIds.removeAll(eixstedProductIds);
        if (requiredIds.isEmpty()) {
            return;
        }

        List<Stock> stocks = requiredIds.stream().map(productId -> {
            Stock stock = new Stock();
            stock.setProductId(productId);
            return stock;
        }).toList();
        service.saveAll(stocks);
    }

    @Override
    public void syncQuantity(long productId) {
        syncQuantity(List.of(productId));
    }

    @Override
    public void syncQuantity(List<Long> productIds) {
        service.syncQuantityByProductIdIn(productIds);
    }

    @Transactional(noRollbackFor = LogicViolationException.class)
    @Override
    public ReservateStockResult processOrderStockReservationRequested(OrderStockReservationRequestedEvent event) {
        long orderId = event.getOrderId();
        long userId = event.getUserId();

        if (allocationRepository.existsByOrderId(orderId)) {
            throw new IdempotentEventException("Allocation", orderId, Instant.now());
        }

        Allocation allocation = new Allocation();
        allocation.setStatus(AllocationStatus.RESERVE);
        allocation.setUserId(userId);
        allocation.setOrderId(orderId);

        allocationRepository.save(allocation);

        List<ValidatedItemSnapshot> vis = event.getItems()
                .stream()
                .sorted(Comparator.comparingLong(OrderItemData::getProductId))
                .map(oi -> new ValidatedItemSnapshot(oi.getOrderItemId(), oi.getProductId(), oi.getQuantity(), null))
                .toList();

        List<OrderReservationLog> logs = new ArrayList<>();
        for (ValidatedItemSnapshot vi : vis) {
            try {
                OrderReservationLog log = transactionService.reserveSingleProduct(orderId, vi);
                logs.add(log);
            } catch (StockReservationException e) {
                throw LogicViolationException.of("Stock", vi.getProductId(), e.getMessage());
            }
        }

        return ReservateStockResult.of(logs, allocation);

    }

    /**
     * Method sẽ tiến hành đảo ngược quá trình reservation của OrderReservationLog
     */
    @Transactional
    @Override
    public Allocation processOrderCreationCompensated(OrderCreationCompensatedEvent event) {
        Optional<Allocation> opt = allocationRepository.findByOrderIdForUpdate(event.getOrderId());
        if (opt.isEmpty()) {
            throw ResourceNotFoundException.of("Allocation", event.getOrderId());
        }

        Allocation allocation = opt.get();

        if (allocation.getStatus() != AllocationStatus.RESERVE) {
            throw InvalidStateException.of(
                    "Allocation",
                    allocation.getId(),
                    allocation.getStatus().name(),
                    AllocationStatus.RESERVE.name());
        }

        List<OrderItemData> items = event.getItems().stream()
                .sorted(Comparator.comparing(OrderItemData::getProductId))
                .toList();

        for (OrderItemData orderItemData : items) {
            transactionService.compensateSingleReservation(event.getOrderId(), orderItemData.getProductId());
        }

        allocation.setStatus(AllocationStatus.RELEASED);
        allocationRepository.save(allocation);
        return allocation;

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(maxAttempts = 3)
    @Override
    public Allocation processOrderCancellationRequested(OrderCancellationRequestedEvent event) {
        final long orderId = event.getOrderId();
        final long userId = event.getUserId();

        Optional<Allocation> opt = allocationRepository.findByOrderIdForUpdate(event.getOrderId());
        if (opt.isEmpty()) {

            Allocation dummy = new Allocation();
            dummy.setOrderId(orderId);
            dummy.setUserId(userId);

            dummy.setStatus(AllocationStatus.RELEASED);
            allocationRepository.saveAndFlush(dummy);
            return dummy;
        }

        Allocation allocation = opt.get();
        AllocationStatus currentStatus = allocation.getStatus();
        if (currentStatus == AllocationStatus.RELEASED) {
            throw new IdempotentEventException("Allocation", allocation.getId(), Instant.now());
        }

        if (currentStatus == AllocationStatus.RESERVE) {

            return processOrderCreationCompensated(
                    new OrderCreationCompensatedEvent(
                            event.getOrderId(),
                            event.getUserId(),
                            event.getItems(),
                            "hellow orld"));

        }
        return allocation;

    }

}
