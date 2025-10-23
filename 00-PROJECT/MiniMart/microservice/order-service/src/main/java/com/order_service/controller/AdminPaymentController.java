package com.order_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.order_service.dto.req.RefundReq;
import com.order_service.dto.res.PaymentAdminDetailDTO;
import com.order_service.dto.res.PaymentAdminSummary;
import com.order_service.dto.res.PaymentTransactionDTO;
import com.order_service.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/payments")
public class AdminPaymentController {

    private final PaymentService paymentService;

    @GetMapping("{id}")
    public PaymentAdminDetailDTO getById(@PathVariable long id) {
        return paymentService.findAdminById(id);
    }

    /**
     * Hoàn tiền thủ công. Ko cho thực hiện với API external bên nào hết
     */
    @PostMapping("/{paymentId}/refund")
    public PaymentAdminSummary refund(@PathVariable long paymentId, @Valid @RequestBody RefundReq req) {
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
