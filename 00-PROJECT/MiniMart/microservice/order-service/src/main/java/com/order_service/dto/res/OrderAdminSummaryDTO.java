package com.order_service.dto.res;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.order_service.enums.OrderStatus;
import com.order_service.enums.PaymentMethod;
import com.order_service.model.ShippingMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderAdminSummaryDTO {
    private long id;

    private OrderStatus status;

    private PaymentMethod paymentMethod;

    private ShippingMethod shippingMethod;

    private BigDecimal totalPrice;

    private BigDecimal totalCost;

    private long userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
