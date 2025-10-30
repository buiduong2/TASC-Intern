package com.order_service.dto.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderUpdateReq {

    @NotNull
    @Pattern(regexp = "SHIPPING|COMPLETED")
    private String status;
}
