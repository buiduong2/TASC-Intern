package com.product_service.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.product_service.dto.req.TagFilter;
import com.product_service.dto.req.TagUpdateReq;
import com.product_service.dto.res.TagAdminDTO;
import com.product_service.dto.res.TagAdminDetailDTO;
import com.product_service.service.TagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {@Override
    public Page<TagAdminDTO> findAdminAll(TagFilter filter, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAdminAll'");
    }

    @Override
    public TagAdminDetailDTO findAdminDetailById(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAdminDetailById'");
    }

    @Override
    public TagAdminDetailDTO create(TagUpdateReq dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public TagAdminDetailDTO update(long id, TagUpdateReq dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
