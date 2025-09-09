package com.backend.user.service;

import java.util.function.Function;

import com.backend.user.security.CustomUserDetail;

public interface JwtService {
    String generateAccessToken(CustomUserDetail user);

    String generateRefreshToken(CustomUserDetail user);

    boolean isRefreshTokenValidForUser(String token, CustomUserDetail user);

    CustomUserDetail parseAndValidateAccess(String token, Function<Long, Long> getUserVersion);

}
