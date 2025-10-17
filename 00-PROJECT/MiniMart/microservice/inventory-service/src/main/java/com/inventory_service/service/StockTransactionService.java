package com.inventory_service.service;

import com.inventory_service.model.OrderReservationLog;

public interface StockTransactionService {

    OrderReservationLog reserveSingleProduct(long orderId, long productId, int quantity);

    void compensateSingleReservation(long orderId, long productId);

}
