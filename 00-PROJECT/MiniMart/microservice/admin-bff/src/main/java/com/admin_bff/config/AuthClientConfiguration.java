package com.admin_bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;

public class AuthClientConfiguration {
    private static final String AUTH_HEADER = "Authorization";

    @Bean
    public RequestInterceptor jwtForwardingInterceptor() {
        return template -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();

            if (attributes != null) {
                String jwt = attributes.getRequest().getHeader(AUTH_HEADER);
                if (jwt != null && !jwt.isEmpty()) {
                    // Thêm header Authorization (chứa JWT)
                    template.header(AUTH_HEADER, jwt);
                }
            }
        };
    }
}
