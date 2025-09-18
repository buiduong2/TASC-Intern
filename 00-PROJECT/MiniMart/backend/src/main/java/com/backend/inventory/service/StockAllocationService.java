package com.backend.inventory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.inventory.dto.req.StockAllocationFilter;
import com.backend.inventory.dto.res.StockAllocationDTO;

public interface StockAllocationService {

    Page<StockAllocationDTO> findAll(StockAllocationFilter filter, Pageable pageable);

}
