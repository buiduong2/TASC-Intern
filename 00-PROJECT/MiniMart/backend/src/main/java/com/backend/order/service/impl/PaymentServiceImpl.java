package com.backend.order.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.utils.ErrorCode;
import com.backend.order.dto.req.PaymentTransactionReq;
import com.backend.order.dto.req.RefundReq;
import com.backend.order.dto.res.GatewayResponseData;
import com.backend.order.dto.res.PaymentDTO;
import com.backend.order.dto.res.PaymentGatewayCreateDTO;
import com.backend.order.dto.res.PaymentTransactionDTO;
import com.backend.order.exception.InvalidSignatureException;
import com.backend.order.exception.PaymentAlreadyCompletedException;
import com.backend.order.mapper.PaymentMapper;
import com.backend.order.model.Payment;
import com.backend.order.model.PaymentMethod;
import com.backend.order.model.PaymentStatus;
import com.backend.order.model.PaymentTransaction;
import com.backend.order.model.TransactionStatus;
import com.backend.order.repository.PaymentRepository;
import com.backend.order.repository.PaymentTransactionRepository;
import com.backend.order.service.PaymentGateway;
import com.backend.order.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final Map<String, PaymentGateway> paymentGatewayMap;

    private final PaymentRepository repository;

    private final PaymentTransactionRepository transactionRepository;

    private final PaymentCalculator calculator;

    private final PaymentMapper mapper;

    private PaymentGateway getPaymentGateway(String name) {
        if (!paymentGatewayMap.containsKey(name)) {
            throw new IllegalArgumentException("cannot find paymentgateway");
        }
        return paymentGatewayMap.get(name);
    }

    private PaymentGateway getPaymentGateway(PaymentMethod method) {
        return switch (method) {
            case VNPAY -> paymentGatewayMap.get("vnpay");
            default -> throw new IllegalArgumentException("cannot find paymentgateway");
        };
    }

    private String generateTxnRef() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }

    @Transactional
    @Override
    public PaymentTransactionDTO createTransaction(long paymentId, PaymentTransactionReq req, long userId,
            HttpServletRequest request) {
        Payment payment = repository.findByIdAndUserId(paymentId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PAYMENT_NOT_FOUND.format(paymentId)));
        if (!payment.getStatus().isPending()) {
            throw new PaymentAlreadyCompletedException(ErrorCode.PAYMENT_COMPLETED.format(paymentId));
        }

        String txnRef = generateTxnRef();
        String orderInfo = "Pay for order id =" + payment.getOrder().getId();
        BigDecimal amountDue = payment.getAmountTotal()
                .subtract(payment.getAmountPaid());

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setAmount(amountDue);
        transaction.setPayment(payment);
        transaction.setMethod(PaymentMethod.valueOf(req.getMethod()));
        transaction.setTxnRef(txnRef);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setDescription(orderInfo);

        transactionRepository.save(transaction);

        PaymentGatewayCreateDTO createDTO = getPaymentGateway(req.getMethod().toLowerCase())
                .createTransaction(orderInfo, txnRef, amountDue, request);

        PaymentTransactionDTO dto = mapper.toTransactionDTO(transaction);
        dto.setPaymentUrl(createDTO.getUrl());

        return dto;
    }

    @Transactional
    @Override
    public PaymentTransactionDTO verifyReturn(String gateway, Map<String, String> allParams) {
        PaymentGateway paymentGateway = getPaymentGateway(gateway);
        GatewayResponseData resultDTO = paymentGateway.verifyReturn(allParams);
        if (!resultDTO.isIssignatureValid()) {
            throw new InvalidSignatureException("Invalid " + gateway + " signature");
        }

        PaymentTransaction transaction = transactionRepository.findByTxnRef(resultDTO.getTxnRef())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                ErrorCode.PAYMENT_TRANSACTION_NOT_FOUND.format(resultDTO.getTxnRef())));

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new RuntimeException("Tranasction is not able to pay");
        }

        updateTransaction(resultDTO, transaction);

        calculator.calculateAmountPaid(transaction.getPayment().getId());

        return mapper.toTransactionDTO(transaction);
    }

    private void updateTransaction(GatewayResponseData grd, PaymentTransaction transaction) {
        transaction.setGatewayTxnId(grd.getGatewayTxnId());
        if (grd.isSuccess()) {
            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setPaidAt(LocalDateTime.now());
        } else {
            transaction.setStatus(TransactionStatus.FAILED);
        }
    }

    @Transactional
    @Override
    public Object handleIpn(String gateway, Map<String, String> allParams) {
        PaymentGateway paymentGateway = getPaymentGateway(gateway);
        GatewayResponseData grd = paymentGateway.verifyIpn(allParams);
        if (!grd.isIssignatureValid()) {
            return paymentGateway.getIpnResponse(IpnResponseType.SIGNATURE_NOT_VALID);
        }

        PaymentTransaction transaction = transactionRepository.findByTxnRef(grd.getTxnRef())
                .orElseGet(() -> null);
        if (transaction == null) {
            return paymentGateway.getIpnResponse(IpnResponseType.ORDER_NOT_FOUND);
        }

        if (transaction.getStatus() == TransactionStatus.PENDING) {
            updateTransaction(grd, transaction);
            calculator.calculateAmountPaid(transaction.getPayment().getId());
            return paymentGateway.getIpnResponse(IpnResponseType.USER_NOT_REDIRECTED);
        }

        if (transaction.getStatus() == TransactionStatus.CANCELLED) {
            return paymentGateway.getIpnResponse(IpnResponseType.ORDER_CANCELD);
        }

        if (transaction.getAmount().compareTo(grd.getAmount()) != 0) {
            transaction.setStatus(TransactionStatus.CANCELLED);
            return paymentGateway.getIpnResponse(IpnResponseType.AMOUNT_NOT_VALID);
        }

        if (transaction.getStatus() == TransactionStatus.SUCCESS && !grd.isSuccess()) {
            transaction.setStatus(TransactionStatus.CANCELLED);
            return paymentGateway.getIpnResponse(IpnResponseType.STATUS_NOT_VALID);
        }

        if (transaction.getStatus() == TransactionStatus.FAILED && grd.isSuccess()) {
            transaction.setStatus(TransactionStatus.CANCELLED);
            return paymentGateway.getIpnResponse(IpnResponseType.STATUS_NOT_VALID);
        }

        return paymentGateway.getIpnResponse(IpnResponseType.SUCCESS);
    }

    @Override
    public PaymentDTO findById(long paymentId, long userId) {
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public PaymentDTO findAdminById(long paymentId) {
        throw new UnsupportedOperationException("Unimplemented method 'findAdminById'");
    }

    @Transactional
    @Override
    public PaymentTransactionDTO queryDr(long transactionId) {
        PaymentTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.PAYMENT_TRANSACTION_NOT_FOUND.format(transactionId)));

        PaymentGateway gateway = getPaymentGateway(transaction.getMethod());

        GatewayResponseData grd = gateway.queryDr(transaction.getTxnRef(), transaction.getDescription(),
                transaction.getCreatedAt());

        if (!grd.isIssignatureValid()) {
            throw new InvalidSignatureException("Invalid signature from gateway query Dr response");
        }

        if (transaction.getStatus() == TransactionStatus.PENDING && grd.isSuccess()) {
            updateTransaction(grd, transaction);
            calculator.calculateAmountPaid(transaction.getPayment().getId());

            return mapper.toTransactionDTO(transaction);
        }

        return mapper.toTransactionDTO(transaction);
    }

    @Transactional
    @Override
    public PaymentDTO refund(long paymentId, RefundReq req) {
        Payment payment = repository.findByIdForUpdateAmountPaid(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PAYMENT_NOT_FOUND.format(paymentId)));

        if (payment.getStatus() == PaymentStatus.REFUNDED || payment.getStatus() == PaymentStatus.CANCELLED) {
            return mapper.toDTO(payment);
        }

        // Transaction
        PaymentTransaction transaction = new PaymentTransaction();

        transaction.setAmount(req.getAmount());
        transaction.setMethod(PaymentMethod.valueOf(req.getMethod()));
        transaction.setDescription(req.getReason());
        transaction.setStatus(TransactionStatus.REFUNDED);
        transaction.setPaidAt(LocalDateTime.now());
        transaction.setPayment(payment);
        transaction.setTxnRef("SYSTEM-REFUND-" + generateTxnRef());

        transactionRepository.save(transaction);

        // Payment
        calculator.calculateAmountPaidAfterRefund(payment, transaction);
        repository.save(payment);

        return mapper.toDTO(payment);
    }

}
