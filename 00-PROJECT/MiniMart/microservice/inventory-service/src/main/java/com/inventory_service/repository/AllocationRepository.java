package com.inventory_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.inventory_service.enums.AllocationStatus;
import com.inventory_service.model.Allocation;

import jakarta.persistence.LockModeType;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            FROM Allocation AS a
            WHERE a.orderId = ?1 AND status = ?2
            """)
    Optional<Allocation> findByOrderIdAndStatusForUpdate(long orderId, AllocationStatus status);
}
