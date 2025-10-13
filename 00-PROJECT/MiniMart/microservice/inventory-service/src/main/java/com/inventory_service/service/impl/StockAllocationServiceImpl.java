package com.inventory_service.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.inventory_service.dto.req.StockAllocationFilter;
import com.inventory_service.dto.res.StockAllocationSummaryDTO;
import com.inventory_service.service.StockAllocationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockAllocationServiceImpl implements StockAllocationService {

    @Override
    public Page<StockAllocationSummaryDTO> findAll(StockAllocationFilter filter, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}
