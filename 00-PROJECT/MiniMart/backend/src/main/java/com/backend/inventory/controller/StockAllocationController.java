package com.backend.inventory.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.inventory.dto.req.StockAllocationFilter;
import com.backend.inventory.dto.res.StockAllocationDTO;
import com.backend.inventory.service.StockAllocationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/allocations")
@RequiredArgsConstructor
public class StockAllocationController {

    private final StockAllocationService service;

    @GetMapping
    public Page<StockAllocationDTO> findAll(@Valid StockAllocationFilter filter, @PageableDefault Pageable pageable) {

        return service.findAll(filter,pageable);
    }

}
