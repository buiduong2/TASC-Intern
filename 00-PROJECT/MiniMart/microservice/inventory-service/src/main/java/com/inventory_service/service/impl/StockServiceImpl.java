package com.inventory_service.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.common_kafka.event.shared.dto.OrderItemData;
import com.common_kafka.event.shared.dto.ValidatedItemSnapshot;
import com.common_kafka.event.shared.res.SagaResult;
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

    @Override
    public SagaResult<ReservateStockResult> processProductValidationEvent(ProductValidationPassedEvent event) {
        long orderId = event.getOrderId();
        long userId = event.getUserId();

        Allocation allocation = new Allocation();
        allocation.setStatus(AllocationStatus.RESERVE);
        allocation.setUserId(userId);
        allocation.setOrderId(orderId);

        allocationRepository.save(allocation);

        List<ValidatedItemSnapshot> vis = event.getValidatedItems()
                .stream()
                .sorted(Comparator.comparingLong(ValidatedItemSnapshot::getProductId))
                .toList();

        try {
            List<OrderReservationLog> logs = new ArrayList<>();
            for (ValidatedItemSnapshot vi : vis) {
                OrderReservationLog log = transactionService.reserveSingleProduct(orderId, vi);
                logs.add(log);
            }

            return SagaResult.success(ReservateStockResult.of(logs, allocation));
        } catch (StockReservationException e) {
            return SagaResult.failure(e.getMessage());
        } catch (Exception e) {
            return SagaResult.failure("Server Error");
        }

    }

    /**
     * Method sẽ tiến hành đảo ngược quá trình reservation của OrderReservationLog
     */
    @Transactional
    @Override
    public void processOrderCreationCompensated(OrderCreationCompensatedEvent event) {
        Optional<Allocation> opt = allocationRepository
                .findByOrderIdAndStatusForUpdate(event.getOrderId(), AllocationStatus.RESERVE);
        if (opt.isEmpty()) {
            return;
        }

        List<OrderItemData> items = event.getItems().stream()
                .sorted(Comparator.comparing(OrderItemData::getProductId))
                .toList();

        for (OrderItemData orderItemData : items) {
            transactionService.compensateSingleReservation(event.getOrderId(), orderItemData.getProductId());
        }

        Allocation allocation = opt.get();
        allocation.setStatus(AllocationStatus.RELEASED);
        allocationRepository.save(allocation);

    }

}
