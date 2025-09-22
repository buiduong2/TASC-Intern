package com.backend.order.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.order.dto.req.RefundReq;
import com.backend.order.dto.res.PaymentDTO;
import com.backend.order.dto.res.PaymentTransactionDTO;
import com.backend.order.service.PaymentService;

import jakarta.validation.Valid;
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

    // Hoàn tiền thủ công. Ko cho thực hiện với API bên nào hết

    @PostMapping("/{paymentId}/refund")
    public PaymentDTO refund(@PathVariable long paymentId, @Valid @RequestBody RefundReq req) {
        return paymentService.refund(paymentId, req);
    }

    /**
     * KIểm tra một transaction một cách thủ công
     * 
     * - Cập nhật trạng thái transaction nếu nó chưa đồng bộ
     */
    @GetMapping("/transactions/query-dr/{transactionId}")
    public PaymentTransactionDTO queryDr(@PathVariable long transactionId) {
        return paymentService.queryDr(transactionId);
    }

}
