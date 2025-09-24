package com.backend.order.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.order.dto.req.PaymentTransactionReq;
import com.backend.order.dto.res.PaymentDTO;
import com.backend.order.dto.res.PaymentTransactionDTO;
import com.backend.order.service.PaymentService;
import com.backend.user.security.CustomUserDetail;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Xem danh sách giao dịch
    @GetMapping("{id}")
    public PaymentDTO getById(@PathVariable long id,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        return paymentService.findById(id, userDetail.getUserId());
    }

    // Tạo thanh toán
    @PostMapping("{paymentId}/transactions")
    public PaymentTransactionDTO createTransaction(
            @PathVariable long paymentId,
            @Valid @RequestBody PaymentTransactionReq req,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest request) {
        return paymentService.createTransaction(paymentId, req, userDetail.getUserId(), request);
    }

    // Callback từ cổng thanh toán
    @GetMapping("{gateway:vnpay}/return")
    public PaymentTransactionDTO verifyReturn(@PathVariable String gateway,
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
