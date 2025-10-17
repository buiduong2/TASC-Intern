package com.inventory_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.inventory_service.model.AllocationItem;

public interface AllocationItemRepository extends JpaRepository<AllocationItem, Long> {
    @Query("""
            SELECT DISTINCT ai.purchaseItem.id
            FROM AllocationItem ai
            WHERE ai.purchaseItem.purchase.id = :purchaseId
            """)
    List<Long> getAllocationPurchaseItemIdByPurchaseId(Long purchaseId);

    boolean existsByPurchaseItemId(long id);
}
