package com.backend.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.inventory.model.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long>, CustomPurchaseRepository {

    @Query("""
            SELECT DISTINCT p
            FROM Purchase AS p
            FETCH JOIN p.purchaseItems
            WHERE id = ?1
            """)
    Optional<Purchase> findByIdForDelete(long id);

}
