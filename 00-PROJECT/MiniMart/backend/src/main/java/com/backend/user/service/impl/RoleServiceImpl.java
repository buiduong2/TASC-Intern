package com.backend.user.service.impl;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.backend.user.dto.res.RoleAdminDTO;
import com.backend.user.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    @Override
    public List<RoleAdminDTO> findAllAdmin(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllAdmin'");
    }
    
}
