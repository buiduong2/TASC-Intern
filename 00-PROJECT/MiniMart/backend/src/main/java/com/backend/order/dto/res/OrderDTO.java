package com.backend.order.dto.res;

import java.time.LocalDateTime;

import com.backend.order.model.OrderStatus;
import com.backend.order.model.PaymentMethod;
import com.backend.order.model.ShippingMethod;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {
    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private PaymentMethod paymentMethod;

    private OrderStatus status;

    private ShippingMethod shippingMethod;

    private double shippignCost;

    private double total;
}
