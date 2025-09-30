package com.authentication_service.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import jakarta.annotation.Nullable;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, CustomJwtAuthenticationToken> {

    @Override
    @Nullable
    public CustomJwtAuthenticationToken convert(@SuppressWarnings("null") Jwt source) {
        List<String> authorities = source.getClaim("authorities");
        List<String> scopes = source.getClaim("scope");
        Long userId = source.getClaim("userId");

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        for (String scope : scopes) {
            grantedAuthorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
        }

        for (String authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority));
        }

        return new CustomJwtAuthenticationToken(source, grantedAuthorities, userId);
    }
}
