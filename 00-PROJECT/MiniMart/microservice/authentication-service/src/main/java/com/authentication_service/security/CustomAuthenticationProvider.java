package com.authentication_service.security;

import java.util.List;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.FieldError;

import com.authentication_service.dto.req.LoginReq;
import com.authentication_service.exception.AuthValidationException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    private final Validator validator;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService, Validator validator) {
        super(userDetailsService);
        this.validator = validator;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        LoginReq loginReq = new LoginReq();
        loginReq.setUsername(username);
        loginReq.setPassword(password);

        Set<ConstraintViolation<LoginReq>> violations = validator.validate(loginReq);

        if (!violations.isEmpty()) {
            List<FieldError> fieldErrors = violations.stream()
                    .map(vi -> new FieldError("loginReq", vi.getPropertyPath().toString(), vi.getMessage())).toList();

            throw new AuthValidationException("Invalid login request", fieldErrors);
        }

        return super.authenticate(authentication);

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
