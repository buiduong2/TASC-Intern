package com.backend.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.backend.order.model.Payment;

import jakarta.persistence.LockModeType;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
            SELECT p
            FROM Order AS o
            LEFT JOIN o.payment AS p
            LEFT JOIN o.customer AS c
            WHERE p.id = ?1 AND c.id = ?2
            """)
    Optional<Payment> findByIdAndUserId(long id, long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT p
            FROM Payment AS p
            LEFT JOIN FETCH p.transactions
            WHERE p.id = ?1
            """)
    Optional<Payment> findByIdForUpdateAmountPaid(long id);

}
