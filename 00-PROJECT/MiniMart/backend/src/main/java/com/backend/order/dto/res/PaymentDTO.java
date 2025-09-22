package com.backend.order.dto.res;

import java.time.LocalDateTime;

import com.backend.order.model.PaymentMethod;
import com.backend.order.model.PaymentStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDTO {
    private Long id;
    private PaymentMethod name;
    private PaymentStatus status;
    private LocalDateTime completedAt;
    private double amountTotal;
    private double amountPaid;
    private long orderId;
}
