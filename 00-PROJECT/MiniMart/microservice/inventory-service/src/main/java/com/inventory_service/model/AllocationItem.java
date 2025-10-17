package com.inventory_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AllocationItem {
    @Id
    @GeneratedValue
    private Long id;

    private int totalAllocatedQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    private PurchaseItem purchaseItem;

    @ManyToOne(fetch = FetchType.LAZY)
    private Allocation allocation;

}
