package com.backend.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.order.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("FROM Order WHERE id = ?1")
    Optional<Order> findByIdForUpdate(long orderId);
}
