package com.inventory_service.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.inventory_service.enums.StockAllocationStatus;

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
public class StockAllocation {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PurchaseItem purchaseItem;

    @Column(nullable = false)
    private Long orderId;

    private int allocatedQuantity;

    @Enumerated(EnumType.STRING)
    private StockAllocationStatus status;

    @CreatedDate
    private LocalDateTime createdAt;
}
