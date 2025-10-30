package com.order_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.order_service.model.OrderSagaTracker;

import jakarta.persistence.LockModeType;

public interface OrderSagaTrackerRepository extends JpaRepository<OrderSagaTracker, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            FROM OrderSagaTracker AS ost
            WHERE ost.orderId = ?1
            """)
    Optional<OrderSagaTracker> findByOrderIdForUpdate(long orderId);

    Optional<OrderSagaTracker> findByOrderId(long orderId);
}
