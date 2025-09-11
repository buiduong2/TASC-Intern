package com.backend.user.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.backend.user.dto.req.UpdateUserAdminReq;
import com.backend.user.dto.res.UserAdminDTO;
import com.backend.user.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public Page<UserAdminDTO> findAdminDTOsBy(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAdminDTOsBy'");
    }

    @Override
    public UserAdminDTO findAdminById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAdminById'");
    }

    @Override
    public UserAdminDTO update(long id, UpdateUserAdminReq req) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

}
