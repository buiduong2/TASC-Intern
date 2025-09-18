package com.backend.inventory.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.backend.inventory.model.StockAllocation;
import com.backend.inventory.model.StockAllocationStatus;

public interface StockAllocationRepository
        extends JpaRepository<StockAllocation, Long>, JpaSpecificationExecutor<StockAllocation> {

    @Query("""
                FROM StockAllocation sa
                WHERE sa.orderItem.order.id = ?1 AND sa.status = ?2

            """)
    List<StockAllocation> findByOrderId(Long orderId, StockAllocationStatus status);

    boolean existsByPurchaseItemId(long id);

    @Query("""
            SELECT DISTINCT sa.purchaseItem.id
            FROM StockAllocation sa
            WHERE sa.purchaseItem.purchase.id = :purchaseId
            """)
    List<Long> findAllocatedPurchaseItemIdsByPurchaseId(Long purchaseId);

    <T> Page<T> findAll(Specification<StockAllocation> spec, Pageable pageable, Class<T> type);

}
