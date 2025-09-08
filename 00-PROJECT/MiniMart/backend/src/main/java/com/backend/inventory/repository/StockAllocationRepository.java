package com.backend.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.inventory.model.StockAllocation;
import com.backend.inventory.model.StockAllocationStatus;

public interface StockAllocationRepository extends JpaRepository<StockAllocation, Long> {

    @Query("""
                FROM StockAllocation sa
                WHERE sa.orderItem.order.id = ?1 AND sa.status = ?2

            """)
    List<StockAllocation> findByOrderId(Long orderId,StockAllocationStatus status);

}
