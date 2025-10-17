package com.inventory_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.inventory_service.enums.OrderReservationLogStatus;
import com.inventory_service.model.OrderReservationLog;

import jakarta.persistence.LockModeType;

public interface OrderReservationLogRepository extends JpaRepository<OrderReservationLog, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            FROM OrderReservationLog AS l
            WHERE l.orderId = ?1 AND l.status = ?2
            """)
    List<OrderReservationLog> findByOrderIdAndStatusForAllocation(long orderId, OrderReservationLogStatus status);

}
