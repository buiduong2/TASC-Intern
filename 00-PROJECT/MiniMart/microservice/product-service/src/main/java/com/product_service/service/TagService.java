package com.product_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.product_service.dto.req.TagFilter;
import com.product_service.dto.req.TagUpdateReq;
import com.product_service.dto.res.TagAdminDTO;
import com.product_service.dto.res.TagAdminDetailDTO;

public interface TagService {

    Page<TagAdminDTO> findAdminAll(TagFilter filter, Pageable pageable);

    TagAdminDetailDTO findAdminDetailById(long id);

    TagAdminDetailDTO create(TagUpdateReq dto);

    TagAdminDetailDTO update(long id, TagUpdateReq dto);

    void delete(long id);

}
