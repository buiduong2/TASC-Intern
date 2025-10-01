package com.authentication_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import com.authentication_service.security.CustomAuthenticationProvider;
import com.authentication_service.security.CustomJwtAuthenticationConverter;
import com.authentication_service.security.FederatedIdentityAuthenticationSuccessHandler;
import com.authentication_service.service.impl.UserDetailServiceImpl;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    final CustomJwtAuthenticationConverter converter;

    final FederatedIdentityAuthenticationSuccessHandler federatedIdentityAuthenticationSuccessHandler;

    final UserDetailServiceImpl userDetailsService;

    final Validator validator;

    final CorsConfigurationSource configurationSource;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationProvider customAuthenticationProvider() {
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(userDetailsService, validator);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    @Order(2)
    SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        String[] resourceUrls = new String[] { "/img/**", "/css/**", "/js/**" };

        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/login").permitAll()
                .requestMatchers("/api/auth/me").authenticated()
                .requestMatchers("/api/auth/change-password").authenticated()
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers(resourceUrls).permitAll()
                .anyRequest().authenticated())
                .cors(c -> c.configurationSource(configurationSource))
                .csrf(c -> c.ignoringRequestMatchers("/api/**"))
                .formLogin(c -> c.loginPage("/login")
                        .failureUrl("/login?error"))
                .oauth2Login(c -> c.successHandler(federatedIdentityAuthenticationSuccessHandler)
                        .loginPage("/login"))
                .oauth2ResourceServer(c -> c.jwt(j -> j.jwtAuthenticationConverter(converter)))
                .logout(Customizer.withDefaults())
                .exceptionHandling(c -> c.accessDeniedPage("/"));

        return http.build();
    }
}
