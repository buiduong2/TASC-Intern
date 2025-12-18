package com.order_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.order_service.dto.req.OrderCreateReq;
import com.order_service.dto.req.OrderFilter;
import com.order_service.dto.req.OrderUpdateReq;
import com.order_service.dto.res.OrderAdminSummaryDTO;
import com.order_service.dto.res.OrderDTO;
import com.order_service.dto.res.OrderDetailDTO;

public interface OrderService {

    Page<OrderDTO> findPage(Pageable pageable, Long userId);

    OrderDetailDTO findByIdAndUserId(long id, Long userId);

    OrderDTO create(OrderCreateReq req, Long userId);

    OrderDTO createFromCart(OrderCreateReq req, Long userId);

    void cancel(Long orderId, Long userId);

    Page<OrderAdminSummaryDTO> findAdminAll(OrderFilter filter, Pageable pageable);

    OrderDTO cancelAdmin(long id);

    OrderDTO updateStatus(long id, OrderUpdateReq req);

}
