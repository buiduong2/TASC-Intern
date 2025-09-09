package com.backend.common.config;

import java.security.Key;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.backend.common.exception.GenericErrorResponse;
import com.backend.user.security.JwtAuthenticationEntryPoint;
import com.backend.user.security.JwtAuthenticationFilter;
import com.backend.user.service.impl.CustomUserDetailService;
import com.backend.user.utils.JwtCodec;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomUserDetailService customUserDetailService;

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Value("${custom.security.jwt.secret}")
    private String secret;

    @Value("${custom.security.jwt.allowed-skew-seconds}")
    private long allowedSkewSeconds;

    @Bean
    JwtCodec jwtCodec() {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        return new JwtCodec(key, allowedSkewSeconds); // 30s clock skew
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            GenericErrorResponse body = GenericErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpServletResponse.SC_FORBIDDEN)
                    .error("Forbidden")
                    .message("Bạn không có quyền truy cập tài nguyên này.")
                    .build();
            new ObjectMapper().writeValue(response.getOutputStream(), body);
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler())
                        .authenticationEntryPoint(authenticationEntryPoint))
                .userDetailsService(customUserDetailService)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorize -> authorize.requestMatchers("/api/auth/login").permitAll()
                                .anyRequest().authenticated()

                );

        return http.build();
    }

}
