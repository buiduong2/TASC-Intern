package com.order_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.order_service.dto.res.PaymentSummaryDTO;
import com.order_service.model.Payment;

import jakarta.persistence.LockModeType;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderIdAndUserId(long orderId, long userId);

    Optional<PaymentSummaryDTO> findByIdAndUserId(long paymentId, long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            FROM Payment  AS p
            LEFT JOIN FETCH p.transactions AS t
            WHERE p.id = ?1 AND p.userId = ?2
            """)
    Optional<Payment> findByIdAndUserIdForUpdate(long paymentId, long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            FROM Payment  AS p
            LEFT JOIN FETCH p.transactions AS t
            WHERE p.id = ?1
            """)
    Optional<Payment> findByIdForUpdate(long paymentId);
}
