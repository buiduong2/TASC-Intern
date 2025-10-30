package com.inventory_service.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.common_kafka.event.shared.dto.ValidatedItemSnapshot;
import com.common_kafka.exception.saga.LogicViolationException;
import com.common_kafka.exception.saga.ResourceNotFoundException;
import com.common_kafka.exception.saga.BusinessInvariantViolationException;
import com.inventory_service.enums.OrderReservationLogStatus;
import com.inventory_service.exception.ErrorCode;
import com.inventory_service.exception.StockReservationException;
import com.inventory_service.model.OrderReservationLog;
import com.inventory_service.model.Stock;
import com.inventory_service.repository.OrderReservationLogRepository;
import com.inventory_service.repository.StockRepository;
import com.inventory_service.service.StockTransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockTransactionServiceImpl implements StockTransactionService {

    private final StockRepository stockRepository;
    private final OrderReservationLogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public OrderReservationLog reserveSingleProduct(long orderId, ValidatedItemSnapshot vi) {
        long productId = vi.getProductId();
        Stock stock = stockRepository.findByProductIdForUpdate(productId)
                .orElseThrow(
                        () -> new StockReservationException(
                                ErrorCode.STOCK_RESERVATION_PRODUCT_NOT_FOUND,
                                productId));

        int availableQuantity = stock.getAvaiableQuantity();
        int requestedQuantity = vi.getQuantity();

        if (availableQuantity < requestedQuantity) {
            throw new StockReservationException(
                    ErrorCode.STOCK_RESERVATION_INSUFFICIENT,
                    productId);
        }

        stock.setPendingReservation(stock.getPendingReservation() + requestedQuantity);
        stockRepository.save(stock);

        // Log
        OrderReservationLog log = new OrderReservationLog();
        log.setOrderId(orderId);
        log.setProductId(productId);
        log.setQuantityReserved(requestedQuantity);
        log.setOrderItemId(vi.getOrderItemId());
        log.setStatus(OrderReservationLogStatus.RESERVED);
        logRepository.save(log);
        return log;

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void compensateSingleReservation(long orderId, long productId) {
        OrderReservationLog log = logRepository.findByOrderIdAndProductIdAndStatusForCompenstate(orderId, productId)
                .orElseGet(() -> null);
        if (log == null || log.getStatus() != OrderReservationLogStatus.RESERVED) {
            return;
        }

        Stock stock = stockRepository.findByProductIdForUpdate(productId)
                .orElseThrow(() -> ResourceNotFoundException.of("stock", productId));

        int reserveQuantity = log.getQuantityReserved();

        stock.setPendingReservation(stock.getPendingReservation() - reserveQuantity);

        if (stock.getPendingReservation() < 0) {
            throw new BusinessInvariantViolationException("Stock", stock.getId(), "Stock has pendign reservation < 0");
        }

        stockRepository.save(stock);

        log.setStatus(OrderReservationLogStatus.COMPENSATED);
        logRepository.save(log);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void commitSingleProduct(OrderReservationLog log) {
        if (log.getStatus() != OrderReservationLogStatus.RESERVED) {
            return;
        }

        Long productId = log.getProductId();
        Stock stock = stockRepository.findByProductIdForUpdate(productId)
                .orElseThrow(
                        () -> new StockReservationException(ErrorCode.STOCK_RESERVATION_PRODUCT_NOT_FOUND, productId));

        int totalQuantity = stock.getTotalQuantity();
        int reserveQuantity = log.getQuantityReserved();

        stock.setPendingReservation(stock.getPendingReservation() - reserveQuantity);
        stock.setCommittedAllocation(stock.getCommittedAllocation() + reserveQuantity);

        if (stock.getPendingReservation() < 0) {
            throw new StockReservationException(
                    ErrorCode.STOCK_RESERVATION_INSUFFICIENT,
                    productId);
        }

        if (stock.getCommittedAllocation() + stock.getPendingReservation() > totalQuantity) {
            throw new StockReservationException(
                    ErrorCode.STOCK_RESERVATION_INSUFFICIENT,
                    productId);
        }

        log.setStatus(OrderReservationLogStatus.COMMITTED);

        stockRepository.save(stock);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void compensateSingleCommitProduct(long orderId, long productId) {
        OrderReservationLog log = logRepository.findByOrderIdAndProductIdAndStatusForCompenstate(orderId, productId)
                .orElseThrow(() -> ResourceNotFoundException.of("Stock", productId));

        if (log.getStatus() != OrderReservationLogStatus.COMMITTED) {
            // Skip
            return;
        }

        Stock stock = stockRepository.findByProductIdForUpdate(productId)
                .orElseThrow(
                        () -> LogicViolationException.of("Stock", productId,
                                "Stock must be pre created but not found"));

        int reserveQuantity = log.getQuantityReserved();

        stock.setCommittedAllocation(stock.getCommittedAllocation() - reserveQuantity);

        if (stock.getCommittedAllocation() < 0) {
            throw new BusinessInvariantViolationException("Stock", stock.getId(), "Stock has CommittedAllocation < 0");
        }

        stockRepository.save(stock);

        log.setStatus(OrderReservationLogStatus.COMPENSATED);
        logRepository.save(log);
    }
}
