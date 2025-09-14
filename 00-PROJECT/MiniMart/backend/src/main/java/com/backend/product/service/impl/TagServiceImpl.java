package com.backend.product.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.product.dto.req.TagFilter;
import com.backend.product.dto.req.TagUpdateReq;
import com.backend.product.dto.res.TagAdminDTO;
import com.backend.product.dto.res.TagAdminDetailDTO;
import com.backend.product.mapper.TagMapper;
import com.backend.product.model.Tag;
import com.backend.product.repository.JdbcTagRepository;
import com.backend.product.repository.TagRepository;
import com.backend.product.service.TagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    private final JdbcTagRepository jdbcTagRepository;

    @Override
    public Page<TagAdminDTO> findAdminAll(TagFilter filter, Pageable pageable) {
        return jdbcTagRepository.findAllAdmin(filter, pageable);
    }

    @Override
    public TagAdminDetailDTO create(TagUpdateReq dto) {
        Tag entity = new Tag();
        tagMapper.updateEntityFromDto(dto, entity);
        Tag saved = tagRepository.save(entity);

        return tagMapper.toAdminDTO(saved);

    }

    @Override
    public TagAdminDetailDTO update(long id, TagUpdateReq dto) {
        Tag entity = jdbcTagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));
        tagMapper.updateEntityFromDto(dto, entity);
        Tag saved = jdbcTagRepository.save(entity);
        return tagMapper.toAdminDTO(saved);

    }

    @Override
    public void delete(long id) {
        jdbcTagRepository.deleteById(id);
    }

    @Override
    public TagAdminDetailDTO findAdminDetailById(long id) {
        return tagRepository.findById(id)
                .map(tagMapper::toAdminDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));
    }

}
