package com.order_service.dto.res;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.order_service.enums.OrderStatus;
import com.order_service.enums.PaymentStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {

    private Long id;

    private BigDecimal totalPrice;

    private Long paymentId;

    private Long userId;

    private OrderStatus status;

    private PaymentStatus paymentStatus;

    private String shippingMethod;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
