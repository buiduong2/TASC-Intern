package com.backend.order.service;

import java.util.Collection;
import java.util.List;

import com.backend.order.dto.req.OrderItemReq;
import com.backend.order.model.Order;
import com.backend.order.model.OrderItem;

public interface OrderItemService {
    /**
     * FIFO - Đừng nhập kho kiểu lắt nhắt làm tụt performance
     * - Product
     * 
     */
    List<OrderItem> create(Order order, Collection<OrderItemReq> dtos);

    void releaseStockAllocation(Order order);
}
