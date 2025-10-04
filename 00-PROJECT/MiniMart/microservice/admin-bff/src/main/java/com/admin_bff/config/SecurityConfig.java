package com.admin_bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.common.security.InternalHeaderAuthenticationFilter;
import com.common.security.InternalHeaderAuthenticationProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    InternalHeaderAuthenticationFilter internalHeaderAuthenticationFilter() {
        return new InternalHeaderAuthenticationFilter();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        return new InternalHeaderAuthenticationProvider();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(internalHeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(SessionManagementConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorize -> authorize
                                .anyRequest().authenticated());

        return http.build();
    }
}
