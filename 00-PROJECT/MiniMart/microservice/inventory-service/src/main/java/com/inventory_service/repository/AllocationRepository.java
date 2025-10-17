package com.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory_service.model.Allocation;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {

}
