package com.backend.order.dto.res;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.backend.order.model.PaymentMethod;
import com.backend.order.model.PaymentStatus;
import com.backend.order.model.TransactionStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentAdminDetailDTO {
    private Long id;
    private PaymentMethod name;
    private PaymentStatus status;
    private LocalDateTime completedAt;
    private BigDecimal amountTotal;
    private BigDecimal amountPaid;
    private long orderId;

    private List<PaymentTransactionDTO> transactions;

    @Getter
    @Setter
    public static class PaymentTransactionDTO {
        private Long id;
        private BigDecimal amount;
        private PaymentMethod method;
        private String description;
        private TransactionStatus status;
        private String txnRef;
        private String gatewayTxnId;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime paidAt;
    }
}
