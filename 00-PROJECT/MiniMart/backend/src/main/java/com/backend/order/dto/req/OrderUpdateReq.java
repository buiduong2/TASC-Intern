package com.backend.order.dto.req;

import com.backend.common.validation.EnumValue;
import com.backend.order.model.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderUpdateReq {
    
    @EnumValue(enumClass = OrderStatus.class)
    @NotNull
    private String status;
}
