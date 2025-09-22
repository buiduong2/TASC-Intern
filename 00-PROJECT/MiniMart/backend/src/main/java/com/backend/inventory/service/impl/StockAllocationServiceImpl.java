package com.backend.inventory.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.backend.inventory.dto.req.StockAllocationFilter;
import com.backend.inventory.dto.res.StockAllocationDTO;
import com.backend.inventory.mapper.StockMapper;
import com.backend.inventory.repository.StockAllocationRepository;
import com.backend.inventory.service.StockAllocationService;
import com.backend.inventory.utils.StockAllocationSpecs;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockAllocationServiceImpl implements StockAllocationService {

    private final StockAllocationRepository repository;

    private final StockAllocationSpecs specs;

    private final StockMapper mapper;

    @Override
    public Page<StockAllocationDTO> findAll(StockAllocationFilter filter, Pageable pageable) {
        return repository.findAll(specs.byFilter(filter), pageable)
                .map(mapper::toDTO);
    }

}
