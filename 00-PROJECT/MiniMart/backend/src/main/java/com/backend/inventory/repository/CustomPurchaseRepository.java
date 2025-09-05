package com.backend.inventory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.inventory.dto.res.PurchaseDTO;

public interface CustomPurchaseRepository {

    Page<PurchaseDTO> findAdminAll(Pageable pageable);
}