package com.backend.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.user.dto.req.UpdateUserAdminReq;
import com.backend.user.dto.req.UserFilter;
import com.backend.user.dto.res.UserAdminDTO;

public interface UserService {

    Page<UserAdminDTO> findAdminDTOsBy(UserFilter filter, Pageable pageable);

    UserAdminDTO findAdminById(Long id);

    UserAdminDTO update(long id, UpdateUserAdminReq req);

}
