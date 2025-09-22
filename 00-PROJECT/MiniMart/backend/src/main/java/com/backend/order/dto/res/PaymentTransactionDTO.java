package com.backend.order.dto.res;

import java.time.LocalDateTime;

import com.backend.order.model.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentTransactionDTO {

    private long id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String paymentUrl; // chỉ có nếu method cần redirect (VD: VNPAY, MOMO)

    private String txnRef;

    private double amount;

    private TransactionStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    private Long paymentId;

}
