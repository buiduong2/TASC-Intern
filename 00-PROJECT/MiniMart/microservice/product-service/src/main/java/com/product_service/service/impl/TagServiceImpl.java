package com.product_service.service.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.exception.GenericException;
import com.product_service.dto.req.TagFilter;
import com.product_service.dto.req.TagUpdateReq;
import com.product_service.dto.res.TagAdminDTO;
import com.product_service.dto.res.TagAdminDetailDTO;
import com.product_service.event.Action;
import com.product_service.event.TagEvent;
import com.product_service.exception.ErrorCode;
import com.product_service.mapper.TagMapper;
import com.product_service.model.Tag;
import com.product_service.repository.TagRepository;
import com.product_service.service.TagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository repository;
    private final TagMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Page<TagAdminDTO> findAdminAll(TagFilter filter, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAdminAll'");
    }

    @Override
    public TagAdminDetailDTO findAdminDetailById(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAdminDetailById'");
    }

    @Transactional
    @Override
    public TagAdminDetailDTO create(TagUpdateReq dto) {
        Tag tag = mapper.toEntity(dto);
        tag = repository.save(tag);
        eventPublisher.publishEvent(new TagEvent(tag.getId(), Action.CREATED));
        return mapper.toDetailDTO(tag);
    }

    @Override
    public TagAdminDetailDTO update(long id, TagUpdateReq dto) {
        Tag tag = repository.findById(id)
                .orElseThrow(() -> new GenericException(ErrorCode.TAG_NOT_FOUND, id));

        mapper.updateEntity(tag, dto);
        repository.save(tag);
        eventPublisher.publishEvent(new TagEvent(tag.getId(), Action.UPDATED));

        return mapper.toDetailDTO(tag);
    }

    @Transactional
    @Override
    public void delete(long id) {
        repository.deleteProductTagByTagId(id);
        repository.deleteById(id);
        eventPublisher.publishEvent(new TagEvent(id, Action.DELETED));
    }

}
