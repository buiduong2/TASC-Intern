package com.order_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.order_service.enums.SagaStepStatus;
import com.order_service.model.OrderSagaTracker;

public interface OrderSagaTrackerRepository extends JpaRepository<OrderSagaTracker, Long> {

    @Modifying
    @Query("""
            UPDATE OrderSagaTracker AS ost
            SET ost.unitPriceConfirmed = ?2
            WHERE ost.orderId = ?1
            """)
    int updateUnitPriceConfirmed(long orderId, SagaStepStatus status);

    @Modifying
    @Query("""
            UPDATE OrderSagaTracker AS ost
            SET ost.stockReserved = ?2
            WHERE ost.orderId = ?1
            """)
    int updateStockReserved(long orderId, SagaStepStatus status);

    @Modifying
    @Query("""
            UPDATE OrderSagaTracker AS ost
            SET ost.paymentProcessed = ?2
            WHERE ost.orderId = ?1
            """)
    int updatePaymentProcessed(long orderId, SagaStepStatus status);

    @Modifying
    @Query("""
            UPDATE OrderSagaTracker AS ost
            SET ost.stockFulfilled = ?2
            WHERE ost.orderId = ?1
            """)
    int updateStockFulfilled(long orderId, SagaStepStatus status);

    @Query("""
            FROM OrderSagaTracker AS ost
            WHERE ost.orderId = ?1
            """)
    Optional<OrderSagaTracker> findByOrderIdForUpdate(long orderId);
}
