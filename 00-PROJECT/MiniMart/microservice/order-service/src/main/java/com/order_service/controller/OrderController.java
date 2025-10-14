package com.order_service.controller;

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

import com.common.security.InternalHeaderUserDetails;
import com.order_service.dto.req.FromCartGroup;
import com.order_service.dto.req.FromReqGroup;
import com.order_service.dto.req.OrderCreateReq;
import com.order_service.dto.res.OrderDTO;
import com.order_service.dto.res.OrderDetailDTO;
import com.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @GetMapping
    public Page<OrderDTO> findPage(
            @PageableDefault Pageable pageable,
            @AuthenticationPrincipal InternalHeaderUserDetails userDetail) {
        return service.findPage(pageable, userDetail.getId());
    }

    @GetMapping("{id}")
    public OrderDetailDTO findById(@PathVariable long id,
            @AuthenticationPrincipal InternalHeaderUserDetails userDetail) {
        return service.findByIdAndUserId(id, userDetail.getId());
    }

    @PostMapping
    public OrderDTO create(
            @Validated(FromReqGroup.class) @RequestBody OrderCreateReq req,
            @AuthenticationPrincipal InternalHeaderUserDetails userDetail

    ) {
        return service.create(req, userDetail.getId());
    }

    @PostMapping("from-cart")
    public OrderDTO createFromCart(
            @Validated(FromCartGroup.class) @RequestBody OrderCreateReq req,
            @AuthenticationPrincipal InternalHeaderUserDetails userDetail) {
        return service.createFromCart(req, userDetail.getId());
    }

    @PostMapping("{id}/cancel")
    public void delete(@PathVariable Long orderId, @AuthenticationPrincipal InternalHeaderUserDetails userDetail) {
        service.cancel(orderId, userDetail.getId());
    }

}
