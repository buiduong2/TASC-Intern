package com.gateway_server.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    @Nullable
    public CustomJwtAuthenticationToken convert(@SuppressWarnings("null") Jwt source) {
        List<String> authorities = source.getClaim("roles");
        List<String> scopes = source.getClaim("scope");
        Long userId = source.getClaim("userId");

        User user = new User();
        user.setId(userId);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        for (String scope : scopes) {
            user.addRole(scope);
            grantedAuthorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
        }

        for (String authority : authorities) {
            user.addRole(authority);
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + authority));
        }

        return new CustomJwtAuthenticationToken(source, grantedAuthorities, user);
    }

}
