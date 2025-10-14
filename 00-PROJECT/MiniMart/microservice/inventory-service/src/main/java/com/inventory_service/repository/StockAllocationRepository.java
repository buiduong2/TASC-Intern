package com.inventory_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.inventory_service.model.StockAllocation;

public interface StockAllocationRepository extends JpaRepository<StockAllocation, Long> {

    @Query("""
            SELECT DISTINCT sa.purchaseItem.id
            FROM StockAllocation sa
            WHERE sa.purchaseItem.purchase.id = :purchaseId
            """)
    List<Long> getAllocationPurchaseItemIdByPurchaseId(Long purchaseId);

    boolean existsByPurchaseItemId(long id);
}
