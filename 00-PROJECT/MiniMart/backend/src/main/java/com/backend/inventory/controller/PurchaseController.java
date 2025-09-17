package com.backend.inventory.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.backend.inventory.dto.req.CreatePurchaseReq;
import com.backend.inventory.dto.req.UpdatePurchaseReq;
import com.backend.inventory.dto.res.PurchaseDTO;
import com.backend.inventory.service.PurchaseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
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

    @PutMapping("{id}")
    public PurchaseDTO update(@PathVariable long id, @Valid @RequestBody UpdatePurchaseReq req) {
        return service.update(id, req);
    }

    /**
     * CHo phpes delete khi remaining_quantity == quantity
     * 
     * - Khi xóa xóa tất cả cả purchaseItem và purchase 
     * 
     * - Kịch bản : 
     *  - Nhập sai
     * - Hủy kèo với nhà cung cấp (trả hàng)
     * - Nhập nhầm
     * - 
     * 
     */
    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
        return;
    }
}
