package com.order_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.order_service.enums.PaymentMethod;
import com.order_service.enums.PaymentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    private PaymentMethod name;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime completedAt;

    @Column(precision = 19, scale = 2)
    private BigDecimal amountTotal;

    /** Snapshot */
    @Column(precision = 19, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<PaymentTransaction> transactions;
}
