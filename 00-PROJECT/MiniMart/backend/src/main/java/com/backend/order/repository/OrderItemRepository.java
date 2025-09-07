package com.backend.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.order.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
