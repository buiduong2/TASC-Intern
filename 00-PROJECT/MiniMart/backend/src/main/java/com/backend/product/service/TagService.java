package com.backend.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.product.dto.req.TagUpdateReq;
import com.backend.product.dto.res.TagAdminDTO;
import com.backend.product.dto.res.TagAdminDetailDTO;

public interface TagService {

    Page<TagAdminDTO> findAdminAll(Pageable pageable);

    TagAdminDetailDTO findAdminDetailById(long id);

    TagAdminDetailDTO create(TagUpdateReq dto);

    TagAdminDetailDTO update(long id, TagUpdateReq dto);

    void delete(long id);

}
