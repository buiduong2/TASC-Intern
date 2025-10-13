package com.inventory_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.inventory_service.dto.req.AdjustmentPurchaseItemReq;
import com.inventory_service.dto.req.ReturnedPurchaseItemReq;
import com.inventory_service.dto.req.UpdatePurchaseItemReq;
import com.inventory_service.dto.res.PurchaseItemDTO;

public interface PurchaseItemService {

    Page<PurchaseItemDTO> findByPurchaseId(long id, Pageable pageable);

    PurchaseItemDTO update(long id, UpdatePurchaseItemReq req);

    PurchaseItemDTO adjustment(long id, AdjustmentPurchaseItemReq req);

    PurchaseItemDTO returns(long id, ReturnedPurchaseItemReq req);

    void delete(long id);

}
