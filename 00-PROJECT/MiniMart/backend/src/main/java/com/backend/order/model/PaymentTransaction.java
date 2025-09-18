package com.backend.order.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PaymentTransaction {

    @Id
    @GeneratedValue
    private Long id;

    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PaymentMethod method;

    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(length = 100, nullable = false, unique = true)
    private String txnRef;
    private String gatewayTxnId;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Payment payment;

}
