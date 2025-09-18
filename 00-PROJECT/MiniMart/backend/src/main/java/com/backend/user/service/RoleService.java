package com.backend.user.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.backend.user.dto.req.RoleUpdateReq;
import com.backend.user.dto.res.RoleAdminDTO;

public interface RoleService {
    List<RoleAdminDTO> findAllAdmin(Pageable pageable);

    RoleAdminDTO update(long id, RoleUpdateReq req);
}
