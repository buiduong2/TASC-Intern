package com.inventory_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.inventory_service.dto.req.CreatePurchaseReq;
import com.inventory_service.dto.req.PurchaseItemReq;
import com.inventory_service.dto.req.UpdatePurchaseReq;
import com.inventory_service.dto.req.UpdateStatusPurchaseReq;
import com.inventory_service.dto.res.PurchaseDetailDTO;
import com.inventory_service.dto.res.PurchaseItemDTO;
import com.inventory_service.dto.res.PurchaseSummaryDTO;

public interface PurchaseService {

    Page<PurchaseSummaryDTO> findPage(Pageable pageable);

    PurchaseDetailDTO findDetailById(long id);

    PurchaseSummaryDTO create(CreatePurchaseReq req);

    PurchaseSummaryDTO update(long id, UpdatePurchaseReq req);

    void deleteById(Long id);

    PurchaseSummaryDTO updateStatus(long id, UpdateStatusPurchaseReq req);

    PurchaseItemDTO createItem(long id, PurchaseItemReq req);

}
