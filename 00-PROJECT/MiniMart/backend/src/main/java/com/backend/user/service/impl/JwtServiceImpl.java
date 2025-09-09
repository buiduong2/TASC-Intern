package com.backend.user.service.impl;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.backend.user.repository.JwtBlackListRepository;
import com.backend.user.security.CustomUserDetail;
import com.backend.user.service.JwtService;
import com.backend.user.utils.JwtCodec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_TYPE = "typ";
    private static final String CLAIM_VERSION = "ver";
    private static final String CLAIM_UID = "uid";

    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";

    @Value("${custom.security.jwt.access-ttl}")
    private Duration accessTtl;

    @Value("${custom.security.jwt.refresh-ttl}")
    private Duration refreshTtl;

    private final JwtCodec jwtCodec;

    private final JwtBlackListRepository jwtBlackListRepository;

    @Override
    public String generateAccessToken(CustomUserDetail user) {
        List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        Map<String, Object> claims = new HashMap<>();

        claims.put(Claims.SUBJECT, user.getUsername());
        claims.put(CLAIM_UID, user.getUserId());
        claims.put(CLAIM_ROLES, roles);
        claims.put(CLAIM_TYPE, ACCESS);
        claims.put(CLAIM_VERSION, user.getTokenVersion());
        claims.put(Claims.ID, jwtCodec.createJti());

        return jwtCodec.build(claims, accessTtl);
    }

    @Override
    public String generateRefreshToken(CustomUserDetail user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_VERSION, user.getTokenVersion());
        claims.put(CLAIM_UID, user.getUserId());
        claims.put(CLAIM_TYPE, REFRESH);

        claims.put(Claims.SUBJECT, user.getUsername());
        claims.put(Claims.ID, jwtCodec.createJti());
        return jwtCodec.build(claims, refreshTtl);
    }

    @Override
    public boolean isRefreshTokenValidForUser(String token, CustomUserDetail user) {
        try {
            Claims c = jwtCodec.parse(token);
            Long userId = c.get(CLAIM_UID, Long.class);
            if (userId == null || !userId.equals(user.getUserId())) {
                return false;
            }

            String typ = (String) c.get(CLAIM_TYPE);
            if (typ == null || !typ.equals(REFRESH)) {
                return false;
            }
            Long ver = c.get(CLAIM_VERSION, Long.class);
            if (ver == null || !ver.equals(user.getTokenVersion())) {
                return false;
            }

            String jti = (String) c.get(Claims.ID);

            if (jwtBlackListRepository.existsById(jti)) {
                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public CustomUserDetail parseAndValidateAccess(String token, Function<Long, Long> getUserVersion) {

        Claims c = jwtCodec.parse(token);
        String type = c.get(CLAIM_TYPE, String.class);
        if (!ACCESS.equals(type)) {
            throw new JwtException("Invalid token type");
        }
        Long userId = c.get(CLAIM_UID, Long.class);
        Long version = c.get(CLAIM_VERSION, Long.class);
        Long currentVersion = getUserVersion.apply(userId);

        if (currentVersion == null || !currentVersion.equals(version)) {
            throw new BadCredentialsException("Token version mismatch");
        }

        String jti = c.get(Claims.ID, String.class);
        if (jti != null && jwtBlackListRepository.existsById(jti)) {
            throw new BadCredentialsException("Token is blacklisted");
        }

        @SuppressWarnings("unchecked")
        List<String> roles = c.get(CLAIM_ROLES, List.class);
        if (roles == null) {
            roles = List.of();
        }

        String username = c.getSubject();

        return new CustomUserDetail(userId, roles, username, version);

    }

}
