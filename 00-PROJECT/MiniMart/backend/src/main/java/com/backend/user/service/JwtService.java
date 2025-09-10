package com.backend.user.service;

import java.util.function.Function;

import com.backend.user.model.User;
import com.backend.user.security.CustomUserDetail;

import io.jsonwebtoken.Claims;

public interface JwtService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    Claims validateRefreshToken(String token, Function<Long, Long> getUserVersion);

    CustomUserDetail parseAndValidateAccess(String token, Function<Long, Long> getUserVersion);

    void invalidateToken(Claims claims);

}
