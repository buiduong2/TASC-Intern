package com.order_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.order_service.model.Order;

import jakarta.persistence.LockModeType;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(value = Order.namedGraph_WithItem, type = EntityGraphType.FETCH)
    @Query("""
            FROM Order AS o
            WHERE o.id = ?1 AND o.userId = ?2
            """)
    Optional<Order> findWithItemsByIdAndUserIdForUpdate(long id, long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(value = Order.namedGraph_WithItemAndAddress, type = EntityGraphType.FETCH)
    @Query("""
            FROM Order AS o
            WHERE o.id = ?1 AND o.userId = ?2
            """)
    Optional<Order> findByIdAndUserIdForDelete(long id, long userId);
}
