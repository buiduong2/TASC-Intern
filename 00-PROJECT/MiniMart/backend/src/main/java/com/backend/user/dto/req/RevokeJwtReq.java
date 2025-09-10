package com.backend.user.dto.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RevokeJwtReq {
    @NotEmpty
    private String accessToken;
    @NotEmpty
    private String refreshToken;
}
