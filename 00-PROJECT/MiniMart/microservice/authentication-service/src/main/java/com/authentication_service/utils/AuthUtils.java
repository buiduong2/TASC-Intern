package com.authentication_service.utils;

import org.springframework.security.core.Authentication;

import com.authentication_service.security.CustomJwtAuthenticationToken;
import com.authentication_service.security.CustomUserDetails;

public class AuthUtils {
    public static Long getAuthId(Authentication auth) {
        if (auth instanceof CustomJwtAuthenticationToken token) {
            return token.getUserId();
        } else if (auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        } else {
            throw new RuntimeException("ERror sever");
        }
    }
}
