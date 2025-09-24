package com.backend.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.backend.order.model.Payment;

import jakarta.persistence.LockModeType;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
            FROM Payment AS p
            LEFT JOIN FETCH p.order AS o
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT p
            FROM Payment AS p
            LEFT JOIN FETCH p.transactions
            LEFT JOIN FETCH p.order
            WHERE p.id = ?1
            """)
    Optional<Payment> findByIdForRefund(long id);

    @Query("""
            SELECT p
            FROM Payment AS p
            LEFT JOIN FETCH p.transactions
            LEFT JOIN FETCH p.order
            WHERE p.id = ?1
            """)
    Optional<Payment> findByIdForAdminDetailDTO(long paymentId);

    @Query("""
            SELECT p
            FROM Payment AS p
            LEFT JOIN FETCH p.order AS o
            WHERE p.id = ?1 AND  o.customer.user.id = ?2
            """)
    Optional<Payment> findByIdAndUserIdForDetailDTO(long paymentId, long userId);

}
