package com.order_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.order_service.model.PaymentTransaction;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    
    Optional<PaymentTransaction> findByTxnRef(String txnRef);
}
