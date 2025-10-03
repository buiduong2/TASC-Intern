package com.authentication_service.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authentication_service.dto.req.ChangePasswordReq;
import com.authentication_service.dto.req.RegisterReq;
import com.authentication_service.dto.res.UserDTO;
import com.authentication_service.enums.RoleName;
import com.authentication_service.enums.UserStatus;
import com.authentication_service.exception.ErrorCode;
import com.authentication_service.mapper.UserMapper;
import com.authentication_service.model.Role;
import com.authentication_service.model.SystemUser;
import com.authentication_service.repository.RoleRepository;
import com.authentication_service.repository.SystemUserRepository;
import com.authentication_service.repository.UserRepository;
import com.authentication_service.service.AuthService;
import com.common.exception.GenericException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository repository;

    private final SystemUserRepository systemUserRepository;

    private final RoleRepository roleRepository;

    private final UserMapper mapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO getInfo(Long authId) {
        return repository.findByIdForAuth(authId)
                .map(mapper::toDTO)
                .orElseThrow(() -> new GenericException(ErrorCode.USER_NOT_FOUND, authId));
    }

    @Transactional
    @Override
    public UserDTO register(RegisterReq registerReq) {
        SystemUser user = mapper.toSystemUser(registerReq);
        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseThrow(() -> new GenericException(ErrorCode.ROLE_NAME_NOT_FOUND, "CUSTOMER"));

        user.setRoles(List.of(customerRole));
        user.setPassword(passwordEncoder.encode(registerReq.getPassword()));
        user.setStatus(UserStatus.ACTIVE);

        user = repository.save(user);

        return mapper.toDTO(user);

    }

    @Transactional
    @Override
    public void changePassword(ChangePasswordReq req, Long authId) {
        SystemUser user = systemUserRepository.findById(authId)
                .orElseThrow(() -> new GenericException(ErrorCode.USER_NOT_FOUND, authId));

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new GenericException(ErrorCode.AUTH_WRONG_PASSWORD);
        }

        String newPassword = passwordEncoder.encode(req.getPassword());

        user.setPassword(newPassword);
        repository.save(user);
    }

}
