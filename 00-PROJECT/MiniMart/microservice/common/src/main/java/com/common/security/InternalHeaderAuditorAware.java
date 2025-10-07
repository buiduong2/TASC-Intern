package com.common.security;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class InternalHeaderAuditorAware {

    public static Optional<Long> getCurrentAuditorId() {

        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(auth -> {
                    if (auth.getPrincipal() instanceof InternalHeaderUserDetails userDetails) {
                        return userDetails.getId();
                    }

                    return null;

                });
    }
}
