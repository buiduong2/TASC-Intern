package com.backend.order.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.backend.order.model.Order;

import jakarta.persistence.LockModeType;

public interface OrderRepository extends JpaRepository<Order, Long>, CustomOrderRepository {

    @Lock(LockModeType.OPTIMISTIC)
    @Query("FROM Order WHERE id = ?1")
    Optional<Order> findByIdForUpdate(long orderId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("FROM Order WHERE id = ?1 AND customer.user.id = ?2")
    Optional<Order> findByIdAndUserIdForUpdate(Long orderId, long userId);

    @Query("FROM Order AS o  JOIN FETCH o.shippingMethod  JOIN FETCH o.payment  WHERE o.customer.user.id = ?1")
    Page<Order> findByIdAndUserIdForPage(long userId, Pageable pageable);
}
