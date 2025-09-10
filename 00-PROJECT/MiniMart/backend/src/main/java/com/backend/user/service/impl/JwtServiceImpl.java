package com.backend.user.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.user.dto.req.RevokeJwtReq;
import com.backend.user.exception.TokenBlacklistedException;
import com.backend.user.exception.TokenVersionMismatchException;
import com.backend.user.model.JwtBlacklist;
import com.backend.user.model.Role;
import com.backend.user.model.TokenType;
import com.backend.user.model.User;
import com.backend.user.repository.JwtBlackListRepository;
import com.backend.user.security.CustomUserDetail;
import com.backend.user.service.JwtService;
import com.backend.user.utils.JwtClaims;
import com.backend.user.utils.JwtCodec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    public static final String ACCESS = "ACCESS";
    public static final String REFRESH = "REFRESH";

    @Value("${custom.security.jwt.access-ttl}")
    private Duration accessTtl;

    @Value("${custom.security.jwt.refresh-ttl}")
    private Duration refreshTtl;

    private final JwtCodec jwtCodec;

    private final JwtBlackListRepository jwtBlackListRepository;

    private Map<String, Object> buildBaseClaims(User user, String type) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Claims.SUBJECT, user.getUsername());
        claims.put(Claims.ID, jwtCodec.createJti());
        claims.put(JwtClaims.VERSION, user.getTokenVersion());
        claims.put(JwtClaims.UID, user.getId());
        claims.put(JwtClaims.TYPE, type);
        return claims;
    }

    @Override
    public String generateAccessToken(User user) {
        List<String> roles = user.getRoles().stream().map(Role::getName).map(String::valueOf).toList();
        Map<String, Object> claims = buildBaseClaims(user, ACCESS);

        claims.put(JwtClaims.ROLES, roles);
        return jwtCodec.build(claims, accessTtl);
    }

    @Override
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = buildBaseClaims(user, REFRESH);
        return jwtCodec.build(claims, refreshTtl);
    }

    private Claims validateToken(String token, String expectedType, Function<Long, Long> getUserVersion) {
        Claims c = jwtCodec.parse(token);

        String type = c.get(JwtClaims.TYPE, String.class);
        if (!expectedType.equals(type)) {
            throw new JwtException("Invalid token type");
        }

        Long userId = c.get(JwtClaims.UID, Long.class);
        Long version = c.get(JwtClaims.VERSION, Long.class);
        String jti = c.get(Claims.ID, String.class);
        if (userId == null || version == null || jti == null) {
            throw new JwtException("Invalid token payload");
        }

        Long currentVersion = getUserVersion.apply(userId);
        if (!version.equals(currentVersion)) {
            throw new TokenVersionMismatchException("Token version mismatch");
        }

        if (jti != null && jwtBlackListRepository.existsById(jti)) {
            throw new TokenBlacklistedException("Token is blacklisted");
        }

        return c;
    }

    @Override
    public Claims validateRefreshToken(String token, Function<Long, Long> getUserVersion) {
        return validateToken(token, REFRESH, getUserVersion);
    }

    @Override
    public CustomUserDetail parseAndValidateAccess(String token, Function<Long, Long> getUserVersion) {
        Claims c = validateToken(token, ACCESS, getUserVersion);
        @SuppressWarnings("unchecked")
        List<String> roles = c.get(JwtClaims.ROLES, List.class);
        if (roles == null)
            roles = List.of();
        return new CustomUserDetail(c.get(JwtClaims.UID, Long.class), roles,
                c.getSubject(),
                c.get(JwtClaims.VERSION, Long.class));

    }

    @Transactional
    @Override
    public void invalidateToken(RevokeJwtReq req, User user) {

        Claims accessClaims = validateToken(req.getAccessToken(), ACCESS, i -> user.getTokenVersion());
        Claims refreshClaims = validateToken(req.getRefreshToken(), REFRESH, i -> user.getTokenVersion());

        invalidateToken(accessClaims);
        invalidateToken(refreshClaims);

        jwtBlackListRepository.deleteAllExpired(LocalDateTime.now(ZoneId.of("UTC")));
    }

    private void invalidateToken(Claims claims) {

        JwtBlacklist jwtBlacklist = new JwtBlacklist();
        Date expired = claims.getExpiration();
        Date issuedAt = claims.getIssuedAt();
        jwtBlacklist.setCreatedAt(LocalDateTime.ofInstant(issuedAt.toInstant(),
                ZoneId.of("UTC")));
        jwtBlacklist.setExpiredAt(LocalDateTime.ofInstant(expired.toInstant(),
                ZoneId.of("UTC")));
        jwtBlacklist.setId(claims.getId());
        jwtBlacklist.setType(TokenType.valueOf(claims.get(JwtClaims.TYPE, String.class)));

        jwtBlackListRepository.save(jwtBlacklist);
    }

}
