package com.backend.inventory.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.backend.inventory.dto.req.CreatePurchaseReq;
import com.backend.inventory.dto.res.PurchaseDTO;
import com.backend.inventory.service.PurchaseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
@Validated
public class PurchaseController {

    private final PurchaseService service;

    @GetMapping
    public Page<PurchaseDTO> findByPage(@PageableDefault Pageable pageable) {
        return service.findPage(pageable);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public PurchaseDTO create(@Valid @RequestBody CreatePurchaseReq req) {
        return service.create(req);
    }
}
