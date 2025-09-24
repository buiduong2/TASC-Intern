package com.backend.user.service.impl;

import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.user.dto.req.ChangePasswordReq;
import com.backend.user.dto.req.LoginReq;
import com.backend.user.dto.req.RefreshTokenReq;
import com.backend.user.dto.req.RegisterReq;
import com.backend.user.dto.req.RevokeJwtReq;
import com.backend.user.dto.res.AuthRes;
import com.backend.user.dto.res.UserDTO;
import com.backend.user.exception.UserInactiveException;
import com.backend.user.mapper.UserMapper;
import com.backend.user.model.Customer;
import com.backend.user.model.Role;
import com.backend.user.model.RoleName;
import com.backend.user.model.User;
import com.backend.user.model.UserStatus;
import com.backend.user.repository.CustomerRepository;
import com.backend.user.repository.RoleRepository;
import com.backend.user.repository.UserRepository;
import com.backend.user.service.AuthService;
import com.backend.user.service.JwtService;
import com.backend.user.utils.JwtClaims;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;

    private final UserMapper mapper;

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;

    private final JwtService jwtService;

    @Override
    public AuthRes login(LoginReq loginReq) {
        User user = repository.findByUsername(loginReq.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(loginReq.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        if (user.getStatus().equals(UserStatus.INACTIVE)) {
            throw new UserInactiveException("User account is not active");
        }

        return generateJwtTokens(user);
    }

    @Transactional
    @Override
    public AuthRes register(RegisterReq registerReq) {

        User user = mapper.toEntity(registerReq);

        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseThrow(() -> new IllegalStateException("Customer role missing"));

        user.setRoles(List.of(customerRole));
        user.setPassword(passwordEncoder.encode(registerReq.getPassword()));
        user.setStatus(UserStatus.ACTIVE);

        Customer customer = mapper.toCustomerEntity(registerReq);
        customer.setUser(user);

        user = repository.save(user);
        customerRepository.save(customer);

        AuthRes authRes = generateJwtTokens(user);

        return authRes;
    }

    private AuthRes generateJwtTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        AuthRes authRes = buildAuthRes(user, accessToken, refreshToken);
        return authRes;
    }

    private AuthRes buildAuthRes(User user, String accessToken, String refreshToken) {
        AuthRes authRes = new AuthRes();
        authRes.setAccessToken(accessToken);
        authRes.setRefreshToken(refreshToken);
        authRes.setUser(mapper.toDTO(user));
        return authRes;
    }

    @Transactional
    @Override
    public AuthRes refreshToken(RefreshTokenReq req) {
        Claims claims = jwtService.validateRefreshToken(req.getRefreshToken(),
                id -> repository.getTokenVersionByUserId(id));

        Long userId = claims.get(JwtClaims.UID, Long.class);
        User user = repository.findById(userId).orElseThrow(() -> new BadCredentialsException("User missing"));

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new UserInactiveException("User account is not active");
        }

        String accessToken = jwtService.generateAccessToken(user);
        return buildAuthRes(user, accessToken, req.getRefreshToken());
    }

    @Transactional
    @Override
    public void changePassword(ChangePasswordReq req, long userId) {
        User user = repository.findById(userId).orElseThrow(() -> new BadCredentialsException("User missing"));
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String newPassword = passwordEncoder.encode(req.getPassword());

        user.setPassword(newPassword);
        user.setTokenVersion(user.getTokenVersion() + 1);
        repository.save(user);
    }

    @Transactional
    @Override
    public void revoke(RevokeJwtReq req, long userId) {
        User user = repository.findById(userId).orElseThrow(() -> new BadCredentialsException("User missing"));
        jwtService.invalidateToken(req, user);
    }

    @Override
    public UserDTO getInfo(long userId) {
        User user = repository.findByIdForDTO(userId).orElseThrow(() -> new BadCredentialsException("User missing"));
        return mapper.toDTO(user);
    }

}
