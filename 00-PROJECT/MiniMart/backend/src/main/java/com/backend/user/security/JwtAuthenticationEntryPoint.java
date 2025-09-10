package com.backend.user.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.backend.common.exception.GenericErrorResponse;
import com.backend.user.exception.TokenBlacklistedException;
import com.backend.user.exception.TokenVersionMismatchException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        GenericErrorResponse error = GenericErrorResponse
                .builder()
                .timestamp(LocalDateTime.now())
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .error("Unauthorized")
                .build();

        ObjectMapper om = new ObjectMapper();

        Throwable cause = authException.getCause();
        if (cause instanceof ExpiredJwtException) {
            error.setMessage("The token has expired. Please log in again.");
        } else if (cause instanceof SignatureException) {
            error.setMessage("The token signature is invalid.");
        } else if (cause instanceof MalformedJwtException) {
            error.setMessage("The token format is invalid.");
        } else if (cause instanceof UnsupportedJwtException) {
            error.setMessage("The token type is not supported.");
        } else if (cause instanceof TokenVersionMismatchException) {
            error.setMessage("The token version does not match. Please log in again.");
        } else if (cause instanceof TokenBlacklistedException) {
            error.setMessage("The token has been revoked.");
        } else {
            error.setMessage("Authentication failed. Please provide valid credentials.");
        }

        om.writeValue(response.getOutputStream(), error);

    }

}
