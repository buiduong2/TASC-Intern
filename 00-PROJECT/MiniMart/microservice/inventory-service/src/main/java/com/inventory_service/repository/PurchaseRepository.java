package com.inventory_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.inventory_service.enums.PurchaseStatus;
import com.inventory_service.model.Purchase;

import jakarta.persistence.LockModeType;

public interface PurchaseRepository extends JpaRepository<Purchase, Long>, CustomPurchaseRepository {
    @Query("""
            SELECT  p
            FROM Purchase AS p
            LEFT JOIN FETCH p.purchaseItems
            WHERE p.id = ?1
            """)
    Optional<Purchase> findForSummaryById(long id);

    @Query("""
            SELECT  p
            FROM Purchase AS p
            LEFT JOIN FETCH p.purchaseItems
            WHERE p.id = ?1 AND status =?2
            """)
    Optional<Purchase> findForSummaryByIdAndStatus(long id, PurchaseStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT p
            FROM Purchase AS p
            LEFT JOIN FETCH p.purchaseItems
            WHERE p.id =?1
            """)
    Optional<Purchase> findWithItemByIdAndLock(long id);

}
