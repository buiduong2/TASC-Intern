package com.inventory_service.controller;

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

import com.inventory_service.dto.req.CreatePurchaseReq;
import com.inventory_service.dto.req.PurchaseItemReq;
import com.inventory_service.dto.req.UpdatePurchaseReq;
import com.inventory_service.dto.req.UpdateStatusPurchaseReq;
import com.inventory_service.dto.res.PurchaseDetailDTO;
import com.inventory_service.dto.res.PurchaseItemDTO;
import com.inventory_service.dto.res.PurchaseSummaryDTO;
import com.inventory_service.service.PurchaseItemService;
import com.inventory_service.service.PurchaseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService service;
    private final PurchaseItemService itemService;

    @GetMapping
    public Page<PurchaseSummaryDTO> findByPage(@PageableDefault Pageable pageable) {
        return service.findPage(pageable);
    }

    @GetMapping("{id}")
    public PurchaseDetailDTO findById(@PathVariable long id) {
        return service.findDetailById(id);
    }

    @GetMapping("{id}/purchase-items")
    public Page<PurchaseItemDTO> findItemsById(@PathVariable long id, @PageableDefault Pageable pageable) {
        return itemService.findByPurchaseId(id, pageable);
    }

    @PostMapping("{id}/purchase-item")
    public PurchaseItemDTO createItem(@PathVariable long id, @Valid @RequestBody PurchaseItemReq req) {
        return service.createItem(id, req);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public PurchaseSummaryDTO create(@Valid @RequestBody CreatePurchaseReq req) {
        return service.create(req);
    }

    @PutMapping("{id}")
    public PurchaseSummaryDTO update(@PathVariable long id, @Valid @RequestBody UpdatePurchaseReq req) {
        return service.update(id, req);
    }

    @PostMapping("{id}/status")
    public PurchaseSummaryDTO updateStatus(@PathVariable long id, @Valid @RequestBody UpdateStatusPurchaseReq req) {
        return service.updateStatus(id, req);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
        return;
    }

}
