package com.inventory_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory_service.dto.req.StockAllocationFilter;
import com.inventory_service.dto.res.StockAllocationSummaryDTO;
import com.inventory_service.service.AllocationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/allocations")
@RequiredArgsConstructor
public class StockAllocationController {
    private final AllocationService service;

    @GetMapping
    public Page<StockAllocationSummaryDTO> findAll(@Valid StockAllocationFilter filter,
            @PageableDefault Pageable pageable) {
        return service.findAll(filter, pageable);
    }
}
