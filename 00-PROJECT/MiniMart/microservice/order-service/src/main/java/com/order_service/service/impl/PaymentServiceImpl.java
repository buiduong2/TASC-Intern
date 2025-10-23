package com.order_service.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.common.exception.GenericException;
import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.common_kafka.event.sales.order.OrderInitialPaymentRequestedEvent;
import com.common_kafka.event.shared.helper.SagaResultUtils;
import com.common_kafka.event.shared.res.SagaResult;
import com.order_service.dto.req.PaymentTransactionReq;
import com.order_service.dto.req.RefundReq;
import com.order_service.dto.res.GatewayResponseData;
import com.order_service.dto.res.PaymentAdminDetailDTO;
import com.order_service.dto.res.PaymentAdminSummary;
import com.order_service.dto.res.PaymentGatewayCreateDTO;
import com.order_service.dto.res.PaymentSummaryDTO;
import com.order_service.dto.res.PaymentTransactionDTO;
import com.order_service.enums.IpnResponseType;
import com.order_service.enums.PaymentMethod;
import com.order_service.enums.PaymentStatus;
import com.order_service.enums.TransactionStatus;
import com.order_service.exception.ErrorCode;
import com.order_service.mapper.PaymentMapper;
import com.order_service.model.Payment;
import com.order_service.model.PaymentTransaction;
import com.order_service.repository.PaymentRepository;
import com.order_service.repository.PaymentTransactionRepository;
import com.order_service.service.PaymentCalculator;
import com.order_service.service.PaymentGateway;
import com.order_service.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;

    private final PaymentTransactionRepository transactionRepository;

    private final Map<String, PaymentGateway> paymentGatewayMap;

    private final PaymentMapper mapper;

    private final PaymentCalculator calculator;

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

    @Override
    public PaymentSummaryDTO findById(long id, Long userId) {
        return repository.findByIdAndUserId(id, id)
                .orElseThrow(() -> new GenericException(ErrorCode.PAYMENT_NOT_FOUND, id));
    }

    @Override
    public PaymentAdminDetailDTO findAdminById(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Transactional
    @Override
    public PaymentTransactionDTO createTransaction(long paymentId, PaymentTransactionReq req, Long userId,
            HttpServletRequest request) {
        Payment payment = repository.findByIdAndUserIdForUpdate(paymentId, userId)
                .orElseThrow(() -> new GenericException(ErrorCode.PAYMENT_NOT_FOUND, paymentId));
        if (!payment.getStatus().isPending()) {
            throw new GenericException(ErrorCode.PAYMENT_CONFLICT_COMPLETED, paymentId);
        }

        String txnRef = generateTxnRef();
        String orderInfo = "Pay for order id =" + payment.getOrderId();

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

    @Override
    public PaymentAdminSummary refund(long paymentId, RefundReq req) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
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
            throw new GenericException(ErrorCode.GATEWAY_QUERY_DR_INVALID_SIGNATURE);
        }

        if (transaction.getStatus() == TransactionStatus.PENDING && grd.isSuccess()) {
            updateTransaction(grd, transaction);
            calculator.calculateAmountPaid(transaction.getPayment().getId());

            return mapper.toTransactionDTO(transaction);
        }

        return mapper.toTransactionDTO(transaction);
    }

    @Override
    public PaymentTransactionDTO verifyReturn(String gateway, Map<String, String> allParams) {
        PaymentGateway paymentGateway = getPaymentGateway(gateway);
        GatewayResponseData resultDTO = paymentGateway.verifyReturn(allParams);

        if (!resultDTO.isIssignatureValid()) {
            throw new GenericException(ErrorCode.GATEWAY_INVALID_SIGNATURE, gateway);
        }

        PaymentTransaction transaction = transactionRepository.findByTxnRef(resultDTO.getTxnRef())
                .orElseThrow(
                        () -> new GenericException(ErrorCode.PAYMENT_TRANSACTION_NOT_FOUND, resultDTO.getTxnRef()));

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

    @Transactional
    @Override
    public SagaResult<Payment> processInitialPaymentRequest(OrderInitialPaymentRequestedEvent event) {
        return SagaResultUtils.execute(() -> {
            long orderId = event.getOrderId();
            long userId = event.getUserId();

            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setName(PaymentMethod.valueOf(event.getPaymentMethod()));
            payment.setStatus(PaymentStatus.PENDING);
            payment.setAmountTotal(event.getTotalAmount());
            payment.setUserId(userId);
            repository.save(payment);
            return payment;
        });
    }

    @Transactional
    @Override
    public void processOrderCreationCompensated(OrderCreationCompensatedEvent event) {
        Payment payment = repository.findByOrderIdAndUserId(event.getOrderId(), event.getUserId())
                .orElseGet(() -> null);

        if (payment == null || payment.getStatus().equals(PaymentStatus.CANCELLED)) {
            return;
        }

        payment.setStatus(PaymentStatus.CANCELLED);

        return;
    }

}
