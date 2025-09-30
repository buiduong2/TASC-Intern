package com.authentication_service.security;

import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

@Component
public class JwtCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Override
    public void customize(JwtEncodingContext context) {
        if (!OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
            return;
        }
        if (!(context.getPrincipal().getPrincipal() instanceof CustomUserDetails)) {
            throw new RuntimeException("Principal is not CustomUserDetails ");
        }
        context.getClaims().claims((claims) -> {
            CustomUserDetails userDetails = (CustomUserDetails) context.getPrincipal().getPrincipal();
            claims.put("scope", context.getRegisteredClient().getScopes());
            claims.put("roles", userDetails.getPermissions());
            int id = Integer.parseInt(userDetails.getId().toString());
            claims.put("userId", id);
        });
    }

}
