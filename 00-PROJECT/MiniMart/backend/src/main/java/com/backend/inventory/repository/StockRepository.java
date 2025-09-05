package com.backend.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.inventory.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findByProductIdIn(List<Long> productIds);
}