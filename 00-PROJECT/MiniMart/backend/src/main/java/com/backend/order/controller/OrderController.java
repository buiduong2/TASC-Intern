package com.backend.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.order.dto.req.FromCartGroup;
import com.backend.order.dto.req.FromReqGroup;
import com.backend.order.dto.req.OrderCreateReq;
import com.backend.order.dto.res.OrderDTO;
import com.backend.order.dto.res.OrderDetailDTO;
import com.backend.order.service.OrderService;
import com.backend.user.security.CustomUserDetail;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @GetMapping
    public Page<OrderDTO> findPage(
            @PageableDefault Pageable pageable,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        return service.findPage(pageable, userDetail.getUserId());
    }

    @GetMapping("{id}")
    public OrderDetailDTO findById(@PathVariable long id, @AuthenticationPrincipal CustomUserDetail userDetail) {
        return service.findByIdAndUserId(id, userDetail.getUserId());
    }

    @PostMapping
    public OrderDTO create(
            @Validated(FromReqGroup.class) @RequestBody OrderCreateReq req,
            @AuthenticationPrincipal CustomUserDetail userDetail

    ) {
        return service.create(req, userDetail.getUserId());
    }

    @PostMapping("from-cart")
    public OrderDTO createFromCart(
            @Validated(FromCartGroup.class) @RequestBody OrderCreateReq req,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        return service.createFromCart(req, userDetail.getUserId());
    }

    @PostMapping("{id}/cancel")
    public void delete(@PathVariable Long orderId, @AuthenticationPrincipal CustomUserDetail userDetail) {
        service.cancel(orderId, userDetail.getUserId());
    }

}
