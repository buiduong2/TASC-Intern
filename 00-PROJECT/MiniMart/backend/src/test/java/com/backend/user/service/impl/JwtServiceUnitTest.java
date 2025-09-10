package com.backend.user.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import com.backend.user.dto.req.RevokeJwtReq;
import com.backend.user.exception.TokenBlacklistedException;
import com.backend.user.exception.TokenVersionMismatchException;
import com.backend.user.model.JwtBlacklist;
import com.backend.user.model.Role;
import com.backend.user.model.RoleName;
import com.backend.user.model.TokenType;
import com.backend.user.model.User;
import com.backend.user.model.UserStatus;
import com.backend.user.repository.JwtBlackListRepository;
import com.backend.user.security.CustomUserDetail;
import com.backend.user.utils.JwtClaims;
import com.backend.user.utils.JwtCodec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

@ExtendWith(MockitoExtension.class)
public class JwtServiceUnitTest {

    @Spy
    private JwtCodec jwtCodec = new JwtCodec("bafb34140132170059f52920b3cca900733c57ea1dfaa95f2637b8ce00a08653", 30L); // real
                                                                                                                       // codec
    @Mock
    private JwtBlackListRepository jwtBlackListRepository;

    @InjectMocks
    private JwtServiceImpl jwtService;
    private Duration accessTtl = Duration.ofMinutes(15);
    private Duration refreshTtl = Duration.ofDays(7);;

    private User user;

    User setupUser() {
        user = new User();
        user.setId(1L);
        user.setUsername("username");
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.CUSTOMER);
        Role role2 = new Role();
        role2.setId(2L);
        role2.setName(RoleName.STAFF);
        user.setRoles(List.of(role, role2));
        user.setStatus(UserStatus.ACTIVE);
        user.setTokenVersion(100);
        return user;

    }

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(jwtService, "accessTtl", accessTtl);
        ReflectionTestUtils.setField(jwtService, "refreshTtl", refreshTtl);
        setupUser();
    }

    // Generate
    @Test
    void generateAccessToken_givenValidUser_shouldContainRolesAndUid() {

        String accessToken = jwtService.generateAccessToken(user);
        Claims claims = jwtCodec.parse(accessToken);

        assertThat(claims.getSubject()).isEqualTo(user.getUsername());
        assertThat(claims.getId()).isNotNull();
        assertThat(claims.getExpiration()).isAfter(new Date());

        assertThat(claims.get(JwtClaims.TYPE, String.class)).isEqualTo(JwtServiceImpl.ACCESS);
        assertThat(claims.get(JwtClaims.UID, Long.class)).isEqualTo(user.getId());
        assertThat(claims.get(JwtClaims.VERSION, Long.class)).isEqualTo(user.getTokenVersion());
        assertThat((List<?>) claims.get(JwtClaims.ROLES, List.class))
                .isEqualTo(user.getRoles().stream().map(Role::getName).map(String::valueOf).toList());
    }

    @Test
    void generateRefreshToken_givenValidUser_shouldContainUidAndTypeRefresh() {

        String accessToken = jwtService.generateRefreshToken(user);
        Claims claims = jwtCodec.parse(accessToken);

        assertThat(claims.getSubject()).isEqualTo(user.getUsername());
        assertThat(claims.getId()).isNotNull();
        assertThat(claims.getExpiration()).isAfter(new Date());

        assertThat(claims.get(JwtClaims.TYPE, String.class)).isEqualTo(JwtServiceImpl.REFRESH);
        assertThat(claims.get(JwtClaims.UID, Long.class)).isEqualTo(user.getId());
        assertThat(claims.get(JwtClaims.VERSION, Long.class)).isEqualTo(user.getTokenVersion());

    }

    // Validate

    @Test
    void validateRefreshToken_givenValidToken_shouldReturnClaims() {

        String token = jwtService.generateRefreshToken(user);
        Claims claims = jwtService.validateRefreshToken(token, id -> user.getTokenVersion());

        assertThat(claims).isNotNull();

        assertThat(claims.getSubject()).isEqualTo(user.getUsername());
        assertThat(claims.getId()).isNotNull();
        assertThat(claims.getExpiration()).isAfter(new Date());

        assertThat(claims.get(JwtClaims.TYPE, String.class)).isEqualTo(JwtServiceImpl.REFRESH);
        assertThat(claims.get(JwtClaims.UID, Long.class)).isEqualTo(user.getId());
        assertThat(claims.get(JwtClaims.VERSION, Long.class)).isEqualTo(user.getTokenVersion());

    }

    // When update messsage check AuthenticationEntryPoint
    @Test
    void validateToken_givenTokenWithWrongType_shouldThrowJwtException() {

        String accessToken = jwtService.generateAccessToken(user);
        JwtException ex1 = assertThrows(JwtException.class,
                () -> jwtService.validateRefreshToken(accessToken, id -> user.getTokenVersion()));
        assertThat(ex1).hasMessage("Invalid token type");
        String refreshToken = jwtService.generateRefreshToken(user);
        JwtException ex2 = assertThrows(JwtException.class,
                () -> jwtService.parseAndValidateAccess(refreshToken, id -> user.getTokenVersion()));

        assertThat(ex2).hasMessage("Invalid token type");
    }

    @Test
    void validateToken_givenTokenWithMissingClaims_shouldThrowJwtException() {

        String accessToken = jwtService.generateAccessToken(user);

        List<Map.Entry<String, Class<? extends Exception>>> entries = List.of(
                Map.entry(JwtClaims.VERSION, JwtException.class),
                Map.entry(JwtClaims.UID, JwtException.class),
                Map.entry(JwtClaims.TYPE, JwtException.class),
                Map.entry(Claims.ID, JwtException.class)

        );

        for (Map.Entry<String, Class<? extends Exception>> entry : entries) {
            Claims claims = jwtCodec.parse(accessToken);
            String key = entry.getKey();
            claims.remove(key);
            String invalidAccessToken = jwtCodec.build(claims, accessTtl);
            assertThrows(entry.getValue(), () -> {
                jwtService.parseAndValidateAccess(invalidAccessToken, i -> user.getTokenVersion());
            }, "Failed at token: " + entry.getKey());

        }

    }

    @Test
    void validateToken_givenTokenWithMismatchedVersion_shouldThrowTokenVersionMismatchException() {
        String accessToken = jwtService.generateAccessToken(user);

        assertThrows(TokenVersionMismatchException.class, () -> {
            jwtService.parseAndValidateAccess(accessToken, i -> user.getTokenVersion() + 1);
        });
    }

    @Test
    void validateToken_givenBlacklistedToken_shouldThrowTokenBlacklistedException() {

        String accessToken = jwtService.generateAccessToken(user);

        doReturn(true).when(jwtBlackListRepository).existsById(anyString());

        assertThrows(TokenBlacklistedException.class, () -> {
            jwtService.parseAndValidateAccess(accessToken, i -> user.getTokenVersion());
        });
    }

    // Parse
    @Test
    void parseAndValidateAccess_givenValidAccessToken_shouldReturnCustomUserDetail() {

        String accessToken = jwtService.generateAccessToken(user);

        CustomUserDetail userDetail = jwtService.parseAndValidateAccess(accessToken, id -> user.getTokenVersion());
        assertThat(userDetail).isNotNull();
        assertThat(userDetail.getAuthorities().stream().map(SimpleGrantedAuthority::getAuthority).toList())
                .containsAll(user.getRoles().stream().map(Role::getName).map(String::valueOf).toList());
        assertThat(userDetail.getUserId()).isEqualTo(user.getId());
        assertThat(userDetail.getTokenVersion()).isEqualTo(user.getTokenVersion());
        assertThat(userDetail.getUsername()).isEqualTo(user.getUsername());

    }

    @Captor
    private ArgumentCaptor<JwtBlacklist> jwtBlackListCaptor;

    // invalidate
    @Test
    void invalidateToken_givenValidClaims_shouldSaveToBlacklist() {
        String refreshToken = jwtService.generateRefreshToken(user);
        String accessToken = jwtService.generateAccessToken(user);

        doAnswer(invok -> {
            JwtBlacklist entity = invok.getArgument(0);
            entity.setId("FAKE ID");
            return entity;
        }).when(jwtBlackListRepository).save(any());

        RevokeJwtReq req = new RevokeJwtReq();
        req.setAccessToken(accessToken);
        req.setRefreshToken(refreshToken);

        jwtService.invalidateToken(req, user);
        verify(jwtBlackListRepository, times(2)).save(jwtBlackListCaptor.capture());

        List<JwtBlacklist> jwtBlacklists = jwtBlackListCaptor.getAllValues();

        Claims accessClaims = jwtCodec.parse(accessToken);
        Claims refreshClaims = jwtCodec.parse(refreshToken);

        assertThat(jwtBlacklists).hasSize(2);
        assertThat(jwtBlacklists.stream().map(JwtBlacklist::getType).toList())
                .containsAll(List.of(TokenType.ACCESS, TokenType.REFRESH));

        for (JwtBlacklist jwtBlacklist : jwtBlacklists) {
            assertThat(jwtBlacklist).isNotNull();
            assertThat(jwtBlacklist.getId()).isNotNull();

            Claims testClaims;
            if (jwtBlacklist.getType() == TokenType.ACCESS) {
                testClaims = accessClaims;
            } else {
                testClaims = refreshClaims;
            }

            assertThat(jwtBlacklist.getExpiredAt())
                    .isEqualTo(LocalDateTime.ofInstant(testClaims.getExpiration().toInstant(), ZoneId.of("UTC")));
            assertThat(jwtBlacklist.getCreatedAt())
                    .isEqualTo(LocalDateTime.ofInstant(testClaims.getIssuedAt().toInstant(), ZoneId.of("UTC")));
        }

    }

    @Test
    void invalidateToken_givenInValidClaims_shouldThrowHJwtException() {
        String accessToken = jwtService.generateAccessToken(user);
        String expiredToken = jwtCodec.build(jwtCodec.parse(accessToken), Duration.ofSeconds(-1));
        RevokeJwtReq req = new RevokeJwtReq();
        req.setAccessToken(accessToken);
        req.setRefreshToken(expiredToken);

        assertThrows(JwtException.class,
                () -> jwtService.invalidateToken(req, user));

    }
}
