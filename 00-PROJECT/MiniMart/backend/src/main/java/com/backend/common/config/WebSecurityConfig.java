package com.backend.common.config;

import java.time.LocalDateTime;

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
import com.backend.user.model.RoleName;
import com.backend.user.security.JwtAuthenticationEntryPoint;
import com.backend.user.security.JwtAuthenticationFilter;
import com.backend.user.service.impl.CustomUserDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomUserDetailService customUserDetailService;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

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
        final String STAFF = RoleName.STAFF.toString();
        final String ADMIN = RoleName.ADMIN.toString();
        http
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler())
                        .authenticationEntryPoint(authenticationEntryPoint))
                .userDetailsService(customUserDetailService)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                        "/swagger-ui.html")
                                .permitAll()
                                .requestMatchers("/api/orders/**").authenticated()
                                .requestMatchers("/api/admin/**").hasAnyAuthority(STAFF, ADMIN)
                                .requestMatchers("/api/admin/users").hasAuthority(ADMIN)
                                .requestMatchers("/api/auth/change-password").authenticated()
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/products/**").permitAll()
                                .requestMatchers("/api/payments/*/return").permitAll()
                                .requestMatchers("/api/payments/*/ipn").permitAll()
                                .requestMatchers("/api/categories/**").permitAll()
                                .anyRequest().authenticated());
                // .anyRequest().permitAll());
                

        return http.build();
    }

}
