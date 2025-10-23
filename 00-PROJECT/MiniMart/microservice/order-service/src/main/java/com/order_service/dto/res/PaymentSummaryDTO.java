package com.order_service.dto.res;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.order_service.enums.PaymentMethod;
import com.order_service.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentSummaryDTO {

    private Long id;
    private long orderId;
    private long userId;
    private PaymentMethod name;
    private PaymentStatus status;
    private LocalDateTime completedAt;
    private BigDecimal amountTotal;
    private BigDecimal amountPaid;
}
