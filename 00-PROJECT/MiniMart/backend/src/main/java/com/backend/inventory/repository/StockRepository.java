package com.backend.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.backend.inventory.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Modifying
    @Query(value = """
            UPDATE stock
            SET quantity = (
                SELECT COALESCE(SUM(remaining_quantity), 0)
                FROM purchase_item
                WHERE product_id = ?1
            )
            WHERE product_id = ?1
                    """, nativeQuery = true)
    int synckQuantity(long productId);

    @Modifying
    @Query("""
            DELETE FROM Stock s WHERE s.product.id = ?1
            """)
    int deleteByProductId(long productId);


    @Query("""
            FROM Stock AS s
            WHERE s.product.id = ?1
            """)
    Optional<Stock> findByProductId(long productId);
}