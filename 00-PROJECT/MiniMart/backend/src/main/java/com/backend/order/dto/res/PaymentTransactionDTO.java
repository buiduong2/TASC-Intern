package com.backend.order.dto.res;

import java.time.LocalDateTime;

import com.backend.order.model.TransactionStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentTransactionDTO {

    private long id;

    private String paymentUrl; // chỉ có nếu method cần redirect (VD: VNPAY, MOMO)

    private String txnRef;

    private double amount;

    private TransactionStatus status;

    private LocalDateTime createdAt;

    private Long paymentId;

}
