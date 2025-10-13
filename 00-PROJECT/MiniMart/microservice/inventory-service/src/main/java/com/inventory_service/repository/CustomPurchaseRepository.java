package com.inventory_service.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.inventory_service.dto.res.PurchaseDetailDTO;
import com.inventory_service.dto.res.PurchaseSummaryDTO;

public interface CustomPurchaseRepository {
    Page<PurchaseSummaryDTO> findAdminAll(Pageable pageable);

    Optional<PurchaseDetailDTO> findAdminById(long id);
}
