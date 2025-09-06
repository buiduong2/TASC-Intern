package com.backend.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.order.dto.req.OrderCreateReq;
import com.backend.order.dto.res.OrderDTO;
import com.backend.order.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @GetMapping
    public Page<OrderDTO> findPage(Pageable pageable) {
        return service.findPage(pageable);
    }

    @PostMapping
    public OrderDTO create(@Valid @RequestBody OrderCreateReq req) {

        return service.create(req, 1);
    }
}
