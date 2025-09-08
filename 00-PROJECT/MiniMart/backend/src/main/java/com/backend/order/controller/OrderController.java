package com.backend.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.order.dto.req.OrderCreateReq;
import com.backend.order.dto.res.OrderDTO;
import com.backend.order.service.OrderService;
import com.backend.user.model.User;

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

    @PostMapping("{id}/cancel")
    public void delete(@PathVariable Long orderId, User user) {
        // 1. Xác thực quyền sở hữu:
        // - Đảm bảo order thuộc về customer của user hiện tại
        // - Nếu không -> throw AccessDeniedException
        // Example: order.getCustomer().getUser().getId() == user.getId()

        // 2. Kiểm tra phương thức thanh toán:
        // - CASH:
        // + Nếu chưa thanh toán (amountPaid == 0) -> cho phép hủy ngay
        // + Nếu đã thanh toán -> chỉ staff mới được hoàn tiền rồi hủy
        // - CARD:
        // + Người dùng KHÔNG được tự hủy
        // + Chỉ staff/admin thực hiện refund trước rồi mới hủy

        // 3. Kiểm tra trạng thái đơn hàng:
        // - Chỉ cho phép hủy khi status == PENDING
        // - Các trạng thái khác (PAID, SHIPPED, COMPLETED, CANCELED) -> từ chối

        // 4. Nếu pass hết các điều kiện trên -> gọi service cancel:
        service.cancel(orderId);
    }
}
