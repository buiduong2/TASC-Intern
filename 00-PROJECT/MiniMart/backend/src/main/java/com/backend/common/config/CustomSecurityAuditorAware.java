package com.backend.common.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.backend.user.model.User;
import com.backend.user.security.CustomUserDetail;

@Component
public class CustomSecurityAuditorAware implements AuditorAware<User> {

    @SuppressWarnings("null")
    @Override
    public Optional<User> getCurrentAuditor() {

        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(auth -> {
                    if (auth.getPrincipal() instanceof CustomUserDetail customUserDetail) {
                        long userId = customUserDetail.getUserId();
                        User user = new User();
                        user.setId(userId);
                        return user;
                    }

                    return null;

                });
    }

}
