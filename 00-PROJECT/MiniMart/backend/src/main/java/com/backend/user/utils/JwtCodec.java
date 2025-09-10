package com.backend.user.utils;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtCodec {

    private final Key key;

    private final long allowedSkewSeconds;

    public JwtCodec(@Value("${custom.security.jwt.secret}") String secret,
            @Value("${custom.security.jwt.allowed-skew-seconds}") long allowedSkewSeconds) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.allowedSkewSeconds = allowedSkewSeconds;
    }

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
