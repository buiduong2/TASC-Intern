package com.backend.inventory.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.inventory.dto.res.PurchaseDTO;
import com.backend.inventory.dto.res.PurchaseDetailDTO;

public interface CustomPurchaseRepository {

    Page<PurchaseDTO> findAdminAll(Pageable pageable);

    Optional<PurchaseDetailDTO> findAdminById(long id);
}