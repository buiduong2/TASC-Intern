package com.order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.order_service.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
