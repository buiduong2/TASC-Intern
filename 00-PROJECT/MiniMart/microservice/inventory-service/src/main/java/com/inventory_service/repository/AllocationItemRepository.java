package com.inventory_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.inventory_service.model.AllocationItem;

import jakarta.persistence.LockModeType;

public interface AllocationItemRepository extends JpaRepository<AllocationItem, Long> {
    @Query("""
            SELECT DISTINCT ai.purchaseItem.id
            FROM AllocationItem ai
            WHERE ai.purchaseItem.purchase.id = :purchaseId
            """)
    List<Long> getAllocationPurchaseItemIdByPurchaseId(Long purchaseId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            FROM AllocationItem AS ai
            JOIN FETCH ai.purchaseItem AS pi
            WHERE ai.allocation.id = ?1
            """)
    List<AllocationItem> findByAllocationIdForCompensate(long allocationId);

    boolean existsByPurchaseItemId(long id);
}
