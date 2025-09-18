package com.backend.user.service.impl;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.utils.ErrorCode;
import com.backend.user.dto.req.RoleUpdateReq;
import com.backend.user.dto.res.RoleAdminDTO;
import com.backend.user.model.Role;
import com.backend.user.repository.RoleRepository;
import com.backend.user.service.RoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repository;

    @Override
    public List<RoleAdminDTO> findAllAdmin(Pageable pageable) {
        return repository.findBy();
    }

    @Transactional
    @Override
    public RoleAdminDTO update(long id, RoleUpdateReq req) {

        Role role = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ROLE_NOT_FOUND.format(id)));
        role.setDescription(req.getDescription());
        repository.save(role);
        return new RoleAdminDTO(role.getId(), role.getName(), role.getDescription());
    }

}
