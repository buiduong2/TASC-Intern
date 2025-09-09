package com.backend.user.utils;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtCodec {

    private final Key key;

    private final long allowedSkewSeconds;

    public String createJti() {
        return UUID.randomUUID().toString();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(allowedSkewSeconds)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims parseAllowExpired(String token) {
        try {
            return parse(token);
        } catch (ExpiredJwtException ex) {
            return ex.getClaims();
        }
    }

    public String build(Map<String, Object> claims, Duration ttl) {
        Instant issuedAt = Instant.now();
        Instant expireAt = issuedAt.plus(ttl);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(expireAt))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
