package com.authentication_service.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomJwtAuthenticationToken extends JwtAuthenticationToken {
    private Long userId;

    public CustomJwtAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities, Long userId) {
        super(jwt, authorities);
        this.userId = userId;
    }
}
