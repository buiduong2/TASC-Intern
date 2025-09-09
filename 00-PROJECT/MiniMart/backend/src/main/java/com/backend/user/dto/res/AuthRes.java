package com.backend.user.dto.res;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRes {
    
    private String accessToken;

    private String refreshToken;
}
