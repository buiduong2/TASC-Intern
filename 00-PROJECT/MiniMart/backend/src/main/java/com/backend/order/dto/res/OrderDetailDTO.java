package com.backend.order.dto.res;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.backend.order.model.OrderStatus;
import com.backend.order.model.PaymentMethod;
import com.backend.order.model.PaymentStatus;

import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {

    private Long id;
    private OrderStatus status;
    private BigDecimal total;
    private String message;
    private PaymentDTO payment;
    private ShippingDTO shippingMethod;
    private List<OrderItemDTO> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddressDTO {
        private long id;
        private String firstName;
        private String lastName;
        private String phone;
        private String details;
        private String city;
        private String area;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentDTO {
        private Long id;
        private PaymentMethod name;
        private PaymentStatus status;
        private BigDecimal amountPaid;
        private BigDecimal amountTotal;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShippingDTO {
        private Long id;
        private String name;
        private BigDecimal cost;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemDTO {
        private Long id;
        private Long productId;
        private String productName;
        private String productImageUrl;
        private BigDecimal unitPrice;
        private int quantity;

        @Transient
        public BigDecimal getTotalPrice() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}