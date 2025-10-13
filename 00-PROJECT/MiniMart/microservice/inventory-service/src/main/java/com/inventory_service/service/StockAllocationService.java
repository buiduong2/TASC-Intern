package com.inventory_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.inventory_service.dto.req.StockAllocationFilter;
import com.inventory_service.dto.res.StockAllocationSummaryDTO;

public interface StockAllocationService {

    Page<StockAllocationSummaryDTO> findAll(StockAllocationFilter filter, Pageable pageable);

}
