package com.inventory_service.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.common_kafka.event.shared.dto.ValidatedItemSnapshot;
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
    public OrderReservationLog reserveSingleProduct(long orderId, long productId, ValidatedItemSnapshot vi) {
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

    }
}
