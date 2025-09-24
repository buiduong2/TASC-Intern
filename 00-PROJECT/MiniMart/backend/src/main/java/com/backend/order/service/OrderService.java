package com.backend.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.order.dto.req.OrderCreateReq;
import com.backend.order.dto.req.OrderUpdateReq;
import com.backend.order.dto.res.OrderAdminDTO;
import com.backend.order.dto.res.OrderDTO;
import com.backend.order.dto.res.OrderFilter;

public interface OrderService {

    Page<OrderDTO> findPage(Pageable pageable, long userId);

    void cancel(Long orderId, long l);

    OrderDTO create(OrderCreateReq req, long userId);

    Page<OrderAdminDTO> findAdminAll(OrderFilter filter, Pageable pageable);

    OrderDTO createFromCart(OrderCreateReq req, long userId);

    OrderDTO cancelAdmin(long id);

    OrderDTO updateStatus(long id, OrderUpdateReq req);
}
