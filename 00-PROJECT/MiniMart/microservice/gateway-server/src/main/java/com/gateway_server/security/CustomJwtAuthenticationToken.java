package com.gateway_server.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomJwtAuthenticationToken extends JwtAuthenticationToken {
    private User user;

    public CustomJwtAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities, User user) {
        super(jwt, authorities);
        this.user = user;

    }
}