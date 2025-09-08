package com.backend.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.backend.inventory.model.PurchaseItem;

import jakarta.persistence.LockModeType;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            FROM PurchaseItem AS pi
            WHERE pi.remainingQuantity > 0 AND pi.product.id IN ?1
            ORDER BY pi.createdAt,pi.id ASC
            """)
    List<PurchaseItem> findAvaiableByProductIdInForUpdate(List<Long> productIds);

    @Modifying
    @Query("""
            UPDATE PurchaseItem AS pi 
            SET pi.remainingQuantity = pi.remainingQuantity + ?2
            WHERE pi.id =?1 
            """)
    int increaseRemainingQuantity(long purchaseItemId, int delta);
}
