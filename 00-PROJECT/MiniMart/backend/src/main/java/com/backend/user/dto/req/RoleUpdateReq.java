package com.backend.user.dto.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleUpdateReq {
    @NotEmpty
    private String description;
}
