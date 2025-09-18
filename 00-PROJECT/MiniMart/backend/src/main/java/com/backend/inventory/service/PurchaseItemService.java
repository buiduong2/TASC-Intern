package com.backend.inventory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.inventory.dto.req.AdjustmentPurchaseItemReq;
import com.backend.inventory.dto.req.ReturnedPurchaseItemReq;
import com.backend.inventory.dto.req.UpdatePurchaseItemReq;
import com.backend.inventory.dto.res.PurchaseItemDTO;

public interface PurchaseItemService {

    void delete(long id);

    PurchaseItemDTO update(long id, UpdatePurchaseItemReq req);

    PurchaseItemDTO adjustment(long id, AdjustmentPurchaseItemReq req);

    PurchaseItemDTO returns(long id, ReturnedPurchaseItemReq req);

    Page<PurchaseItemDTO> findByPurchaseId(long id, Pageable pageable);
    
    
}
