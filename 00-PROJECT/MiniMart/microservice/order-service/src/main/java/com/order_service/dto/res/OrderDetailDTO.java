package com.order_service.dto.res;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.order_service.enums.OrderStatus;
import com.order_service.enums.PaymentMethod;
import com.order_service.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailDTO {
    private Long id;
    private BigDecimal total;
    private String message;
    private OrderStatus status;

    private Long paymentId;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;

    private ShippingDTO shippingMethod;
    private AddressDTO address;
    private List<OrderItemDTO> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
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
        private BigDecimal unitPrice;
        private int quantity;

        public BigDecimal getTotalPrice() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
