package com.inventory_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.inventory_service.dto.res.PurchaseItemDTO;
import com.inventory_service.enums.PurchaseStatus;
import com.inventory_service.model.PurchaseItem;

import jakarta.persistence.LockModeType;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

    @Modifying
    @Query("""
            DELETE FROM PurchaseItem AS pi
            WHERE pi.purchase.id =?1
                AND pi.quantity = pi.remainingQuantity
                AND SIZE(pi.allocationItems) = 0
            """)
    int deleteByPurchaseIdAndAllocationItemsIsEmpty(long purchaseId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT pi
            FROM PurchaseItem AS pi
            WHERE pi.id = ?1
            """)
    Optional<PurchaseItem> findByIdForUpdate(long id);

    Page<PurchaseItemDTO> findByPurchaseId(long purchaseId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT pi
            FROM PurchaseItem AS pi
            JOIN pi.purchase AS p
            WHERE p.status = ?2 AND pi.productId IN ?1 AND pi.remainingQuantity > 0
            ORDER BY pi.createdAt, pi.id
            """)
    List<PurchaseItem> findByProductIdInAndPurchaseStatusForAllocation(List<Long> productIds, PurchaseStatus status);

}
