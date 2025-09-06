package com.backend.order.dto.req;

import java.util.LinkedHashSet;

import com.backend.order.model.PaymentMethod;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreateReq {

    private Long addressId;

    @NotNull
    private Long shippingMethodId;

    @NotNull
    private PaymentMethod paymentMethod;

    private AddressDTO address;

    private String message;

    @NotEmpty
    @NotNull
    @Valid
    private LinkedHashSet<OrderItemDTO> orderItems;

    @Getter
    @Setter
    public static class AddressDTO {
        private String firstName;

        private String lastName;

        private String email;

        private String phone;

        private String details;

        private String city;

        private String area;

    }

    @Getter
    @Setter
    public static class OrderItemDTO {
        @NotEmpty
        private String productId;

        @Positive
        private int quantity;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((productId == null) ? 0 : productId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            OrderItemDTO other = (OrderItemDTO) obj;
            if (productId == null) {
                if (other.productId != null)
                    return false;
            } else if (!productId.equals(other.productId))
                return false;
            return true;
        }

    }

}
