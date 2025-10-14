package com.order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.order_service.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
