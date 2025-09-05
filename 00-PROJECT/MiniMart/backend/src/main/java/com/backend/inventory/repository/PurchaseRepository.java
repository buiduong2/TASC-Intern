package com.backend.inventory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.inventory.dto.res.PurchaseDTO;
import com.backend.inventory.model.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long>, CustomPurchaseRepository {

    @Query(value = """

            """, nativeQuery = true, countQuery = "")
    Page<PurchaseDTO> findDTOBy(Pageable pageable);
}
