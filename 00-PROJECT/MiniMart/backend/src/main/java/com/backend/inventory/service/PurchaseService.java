package com.backend.inventory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.inventory.dto.req.CreatePurchaseReq;
import com.backend.inventory.dto.req.UpdatePurchaseReq;
import com.backend.inventory.dto.res.PurchaseDTO;
import com.backend.inventory.dto.res.PurchaseDetailDTO;

public interface PurchaseService {

    /**
     * Tạo 1 Purchase + N PurchaseItem.
     * - Bắn sự kiện PurchaseCreatedEvent -> Stock listen
     */
    PurchaseDTO create(CreatePurchaseReq req);

    Page<PurchaseDTO> findPage(Pageable pageable);

    PurchaseDTO update(long id, UpdatePurchaseReq req);

    void deleteById(Long id);

    PurchaseDetailDTO findDetailById(long id);
}
