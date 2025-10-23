package com.inventory_service.service;

import com.common_kafka.event.shared.dto.ValidatedItemSnapshot;
import com.inventory_service.model.OrderReservationLog;

public interface StockTransactionService {

    OrderReservationLog reserveSingleProduct(long orderId, ValidatedItemSnapshot vi);

    void commitSingleProduct(OrderReservationLog log);

    void compensateSingleReservation(long orderId, long productId);

    void compensateSingleCommitProduct(long orderId, long productId);

}
