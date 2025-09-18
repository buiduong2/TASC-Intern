package com.backend.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.order.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long id);

    boolean existsByProductId(long productId);

}
