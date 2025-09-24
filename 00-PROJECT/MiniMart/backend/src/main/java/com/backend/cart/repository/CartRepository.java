package com.backend.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.cart.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(value = Cart.NamedGraph_DTO, type = EntityGraphType.FETCH)
    @Query("""
            FROM Cart AS c WHERE c.user.id = ?1
            """)
    Optional<Cart> findByUserIdForDTO(long userId);

    Optional<Cart> findByUserId(long userId);

    @EntityGraph(value = Cart.NamedGraph_Item, type = EntityGraphType.FETCH)
    @Query("""
            FROM Cart AS c WHERE c.user.id = ?1
            """)
    Optional<Cart> findByUserIdForClearItem(long userId);
}
