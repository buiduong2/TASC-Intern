package com.inventory_service.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.inventory_service.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Modifying
    @Query(value = """
            UPDATE stock t
            SET total_quantity = sub.total_purchased_quantity
            FROM (
                SELECT
                    pi.product_id,
                    COALESCE(SUM(pi.quantity), 0) AS total_purchased_quantity
                FROM purchase_item pi
                INNER JOIN purchase p ON p.id = pi.purchase_id
                WHERE p.status = 'ACTIVE'
                AND pi.product_id IN :productIds
                GROUP BY pi.product_id
            ) AS sub
            WHERE t.product_id = sub.product_id
            """, nativeQuery = true)
    int syncQuantityByProductIdIn(Collection<Long> productIds);

    @Query("""
            SELECT productId
            FROM Stock
            WHERE productId IN ?1
            """)
    List<Long> getProductIdByProductIdIn(List<Long> productIds);

}
