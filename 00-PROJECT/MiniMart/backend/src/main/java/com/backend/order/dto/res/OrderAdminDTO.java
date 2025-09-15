package com.backend.order.dto.res;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class OrderAdminDTO {
    private Long id;

    private String status;

    private String paymentMethod;

    private String shippingMethod;

    private Double totalPrice;

    private Double totalCost;

    private Double revenue;

    private Long customerId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
