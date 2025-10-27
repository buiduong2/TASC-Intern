package com.order_service.dto.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefundReq {

    @NotEmpty
    private String reason;

    @NotNull
    private Boolean isManual;

}
