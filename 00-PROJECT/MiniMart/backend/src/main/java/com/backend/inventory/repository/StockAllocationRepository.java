package com.backend.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.inventory.model.StockAllocation;

public interface StockAllocationRepository extends JpaRepository<StockAllocation, Long> {

}
