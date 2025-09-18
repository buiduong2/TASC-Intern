package com.backend.order.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.order.dto.res.PaymentDTO;
import com.backend.order.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final PaymentService paymentService;

    // Xem danh sách thanh toán của một payment
    @GetMapping("{id}")
    public PaymentDTO getAll(@PathVariable long id) {
        return paymentService.findAdminById(id);
    }

    // Hoàn tiền

    @PostMapping("{id}/refund")
    public Object refund(@PathVariable long id) {
        return null;
    }

}
