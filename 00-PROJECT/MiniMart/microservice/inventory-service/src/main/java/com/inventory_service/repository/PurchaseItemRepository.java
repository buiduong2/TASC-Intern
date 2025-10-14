package com.inventory_service.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.inventory_service.dto.res.PurchaseItemDTO;
import com.inventory_service.model.PurchaseItem;

import jakarta.persistence.LockModeType;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

    @Modifying
    @Query("""
            DELETE FROM PurchaseItem AS pi
            WHERE pi.purchase.id =?1
                AND pi.quantity = pi.remainingQuantity
                AND SIZE(pi.stockAllocations) = 0
            """)
    int deleteByPurchaseIdAndStockAllocationsIsEmpty(long purchaseId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            FROM PurchaseItem AS pi
            WHERE pi.id = ?1
            """)
    Optional<PurchaseItem> findByIdForUpdate(long id);

    Page<PurchaseItemDTO> findByPurchaseId(long purchaseId, Pageable pageable);

}
