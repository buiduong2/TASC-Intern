package com.order_service.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.common.security.InternalHeaderUserDetails;
import com.order_service.dto.req.PaymentTransactionReq;
import com.order_service.dto.res.PaymentSummaryDTO;
import com.order_service.dto.res.PaymentTransactionDTO;
import com.order_service.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Xem thông tin giao dịch của một order cụ thể (Payment - Order 1-1)
     */
    @GetMapping("{id}")
    public PaymentSummaryDTO getById(@PathVariable long id,
            @AuthenticationPrincipal InternalHeaderUserDetails userDetail) {
        return paymentService.findById(id, userDetail.getId());
    }

    // Tạo thanh toán
    @PostMapping("{paymentId}/transactions")
    public PaymentTransactionDTO createTransaction(
            @PathVariable long paymentId,
            @Valid @RequestBody PaymentTransactionReq req,
            @AuthenticationPrincipal InternalHeaderUserDetails userDetail,
            HttpServletRequest request) {
        return paymentService.createTransaction(paymentId, req, userDetail.getId(), request);
    }

    // Callback từ cổng thanh toán
    @GetMapping("{gateway:vnpay}/return")
    public PaymentTransactionDTO verifyReturn(
            @PathVariable String gateway,
            @RequestParam Map<String, String> allParams) {
        return paymentService.verifyReturn(gateway, allParams);
    }

    // IPN
    @GetMapping("{gateway:vnpay}/ipn")
    public Object handleIpn(@PathVariable String gateway,
            @RequestParam Map<String, String> allParams) {
        if (allParams == null || allParams.size() == 0) {
            return "Hello";
        }
        return paymentService.handleIpn(gateway, allParams);
    }
}
