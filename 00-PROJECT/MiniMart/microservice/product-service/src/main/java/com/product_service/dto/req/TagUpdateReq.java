package com.product_service.dto.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagUpdateReq {
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
}
