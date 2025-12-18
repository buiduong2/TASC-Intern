package com.order_service.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.common.exception.GenericException;
import com.common_kafka.event.sales.order.OrderCancellationRequestedEvent;
import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.common_kafka.event.sales.order.OrderInitialPaymentRequestedEvent;
import com.common_kafka.exception.saga.DuplicateResourceException;
import com.common_kafka.exception.saga.IdempotentEventException;
import com.common_kafka.exception.saga.InvalidStateException;
import com.order_service.dto.req.PaymentTransactionReq;
import com.order_service.dto.req.RefundReq;
import com.order_service.dto.res.GatewayResponseData;
import com.order_service.dto.res.PaymentAdminDetailDTO;
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
        return repository.findByIdAndUserId(id, userId)
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
        if (!PaymentStatus.isCreatableTransaction(payment.getStatus())) {
            throw new GenericException(ErrorCode.PAYMENT_TRANSACTION_BLOCKED, paymentId, payment.getStatus().name());
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

    @Transactional
    @Override
    public PaymentTransactionDTO refund(long transactionId, RefundReq req, long userId,
            HttpServletRequest servletRequest) {
        PaymentTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.PAYMENT_TRANSACTION_NOT_FOUND.format(transactionId)));

        if (transaction.getStatus() != TransactionStatus.SUCCESS) {
            throw new GenericException(ErrorCode.PAYMENT_TRANSACTION_REFUND_STATUS, transactionId);
        }

        if (req.getIsManual()) {
            transaction.setStatus(TransactionStatus.MANUAL_REFUNDED);
            transactionRepository.save(transaction);
            calculator.calculateAmountPaidAfterRefund(transaction.getPayment(), transaction);
            return mapper.toTransactionDTO(transaction);
        }

        PaymentGateway gateway = getPaymentGateway(transaction.getMethod());
        GatewayResponseData grd = gateway.refund(transaction, servletRequest, userId);

        if (!grd.isIssignatureValid()) {
            throw new GenericException(ErrorCode.GATEWAY_QUERY_DR_INVALID_SIGNATURE);
        }

        if (!grd.isSuccess()) {
            throw new GenericException(ErrorCode.GATEWAY_REFUND_FAILED);
        }
        transaction.setStatus(TransactionStatus.REFUNDED);
        transactionRepository.save(transaction);
        calculator.calculateAmountPaidAfterRefund(transaction.getPayment(), transaction);
        return mapper.toTransactionDTO(transaction);
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

    @Transactional
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

        transactionRepository.save(transaction);

        calculator.calculateAmountPaid(transaction.getPayment().getId());

        return mapper.toTransactionDTO(transaction);
    }

    private void updateTransaction(GatewayResponseData grd, PaymentTransaction transaction) {
        transaction.setGatewayTxnId(grd.getGatewayTxnId());
        if (grd.isSuccess()) {
            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setPaidAt(grd.getPaidAt());
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
    public Payment processInitialPaymentRequest(OrderInitialPaymentRequestedEvent event) {

        if (repository.existsByOrderId(event.getOrderId())) {
            throw new DuplicateResourceException("Payment", event.getOrderId());
        }

        long orderId = event.getOrderId();
        long userId = event.getUserId();

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setName(PaymentMethod.valueOf(event.getPaymentMethod()));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmountTotal(event.getTotalAmount());
        payment.setUserId(userId);

        try {
            repository.save(payment);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateResourceException("Payment", orderId);
        }

        return payment;
    }

    /**
     * PaymentService:
     * ├─ find payment by orderId
     * ├─ if already cancelled → skip
     * ├─ else set status = CANCELLED
     * ├─ save payment
     * └─ publish PaymentCompensationCompletedEvent ✅
     */
    @Transactional
    @Override
    public Optional<Payment> processOrderCreationCompensated(OrderCreationCompensatedEvent event) {
        Optional<Payment> paymentOpt = repository.findByOrderIdAndUserIdForCompensation(event.getOrderId(),
                event.getUserId());

        if (!paymentOpt.isPresent()) {
            return paymentOpt;
        }

        Payment payment = paymentOpt.get();

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidStateException("Payment",
                    payment.getId(),
                    payment.getStatus().name(),
                    PaymentStatus.PENDING.name());
        }

        payment.setStatus(PaymentStatus.CANCELLED);

        repository.save(payment);

        return paymentOpt;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    @Retryable(maxAttempts = 3)
    public Payment processOrderCancellationRequested(OrderCancellationRequestedEvent event) {
        long orderId = event.getOrderId();
        long userId = event.getUserId();
        Optional<Payment> paymentOpt = repository.findByOrderIdAndUserIdForCompensation(orderId, userId);

        if (paymentOpt.isEmpty()) {
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setUserId(userId);
            payment.setStatus(PaymentStatus.CANCELLED);

            repository.saveAndFlush(payment);
            return payment;
        } else {
            Payment payment = paymentOpt.get();

            PaymentStatus status = payment.getStatus();
            if (PaymentStatus.isCompensatedOrCanceled(status)) {
                throw new IdempotentEventException("Payment", payment.getId(), Instant.now());
            }

            if (PaymentStatus.isEasyCompensation(status)) {
                payment.setStatus(PaymentStatus.CANCELLED);
            }

            if (PaymentStatus.isPaid(status)) {
                payment.setStatus(PaymentStatus.REFUND_REQUIRED);
            }

            repository.save(payment);
            return payment;

        }

    }

}
