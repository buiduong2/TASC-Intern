package com.backend.product.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagUpdateReq {
    private String name;
    private String description;
}
