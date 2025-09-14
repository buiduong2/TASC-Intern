package com.backend.user.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.user.dto.req.UpdateUserAdminReq;
import com.backend.user.dto.req.UserFilter;
import com.backend.user.dto.res.UserAdminDTO;
import com.backend.user.mapper.UserMapper;
import com.backend.user.model.Role;
import com.backend.user.model.User;
import com.backend.user.repository.RoleRepository;
import com.backend.user.repository.UserRepository;
import com.backend.user.service.UserService;
import com.backend.user.utils.UserSpecs;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper mapper;

    private final RoleRepository roleRepository;

    private final UserSpecs userSpecs;

    @Override
    public Page<UserAdminDTO> findAdminDTOsBy(UserFilter filter, Pageable pageable) {
        Specification<User> spec = userSpecs.byFilter(filter);
        if (spec != null) {
            return userRepository.findAll(spec, pageable)
                    .map(mapper::toAdminDTO);
        }
        return userRepository.findAll(pageable)
                .map(mapper::toAdminDTO);
    }

    @Override
    public UserAdminDTO findAdminById(Long id) {
        return userRepository.findById(id)
                .map(mapper::toAdminDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User with id = " + id + " is not found"));
    }

    @Transactional
    @Override
    public UserAdminDTO update(long id, UpdateUserAdminReq req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user with id =" + id + " is not found"));

        mapper.update(user, req);

        List<Role> roles = roleRepository.findAllById(req.getRoleIds());

        if (roles.size() != req.getRoleIds().size()) {
            throw new ResourceNotFoundException("Some roleIds is missing");
        }

        user.setRoles(new ArrayList<>());
        roles.forEach(user::addRole);

        userRepository.save(user);

        return mapper.toAdminDTO(user);
    }

}
