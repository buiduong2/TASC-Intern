package com.backend.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.cart.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("""
            FROM CartItem AS ci
            JOIN ci.cart AS c
            WHERE ci.product.id = ?2 AND c.user.id = ?1
            """)
    Optional<CartItem> findByUserIdAndProductId(long userId, long productId);

}
