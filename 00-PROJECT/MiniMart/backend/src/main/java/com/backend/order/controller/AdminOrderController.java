package com.backend.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.order.dto.req.OrderUpdateReq;
import com.backend.order.dto.res.OrderAdminDTO;
import com.backend.order.dto.res.OrderDTO;
import com.backend.order.dto.res.OrderFilter;
import com.backend.order.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('STAFF','ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public Page<OrderAdminDTO> findAll(@Valid OrderFilter filter, @PageableDefault(size = 10) Pageable pageable) {

        return orderService.findAdminAll(filter, pageable);
    }

    // Hủy đơn hàng
    @PostMapping("{id}")
    public OrderDTO cancelOrder(@PathVariable long id) {
        return orderService.cancelAdmin(id);
    }

    @PostMapping("{id}/process")
    public OrderDTO updateStatus(@PathVariable long id, @Valid @RequestBody OrderUpdateReq req) {
        return orderService.updateStatus(id, req);
    }
}
