package com.backend.inventory.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.inventory.dto.req.AdjustmentPurchaseItemReq;
import com.backend.inventory.dto.req.ReturnedPurchaseItemReq;
import com.backend.inventory.dto.req.UpdatePurchaseItemReq;
import com.backend.inventory.dto.res.PurchaseItemDTO;
import com.backend.inventory.service.PurchaseItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/purchase-items")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('STAFF','ADMIN')")
public class PurchaseItemController {

    private final PurchaseItemService service;

    /**
     * Điều chỉnh do nhập liệu sai
     */
    @PutMapping("{id}")
    public PurchaseItemDTO update(@PathVariable long id, @Valid @RequestBody UpdatePurchaseItemReq req) {
        return service.update(id, req);
    }

    /**
     * Điều chỉnh kho so với thực tế
     */
    @PostMapping("{id}/adjustment")
    public PurchaseItemDTO adjustment(@PathVariable long id, @Valid @RequestBody AdjustmentPurchaseItemReq req) {
        return service.adjustment(id, req);
    }

    /**
     * Trả lại mặt hàng cho supplier
     */
    @PostMapping("{id}/returns")
    public PurchaseItemDTO returns(@PathVariable long id, @Valid @RequestBody ReturnedPurchaseItemReq req) {
        return service.returns(id, req);
    }

    /*
     * - Chưa được xuất kho (tức remaining_quantity == quantity) → có thể xóa.
     * - Hủy phiếu nhập đơn lẻ
     */
    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
