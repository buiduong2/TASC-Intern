package com.backend.product.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.backend.product.dto.req.TagUpdateReq;
import com.backend.product.dto.res.TagAdminDetailDTO;
import com.backend.product.model.Tag;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TagMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateEntityFromDto(TagUpdateReq dto, @MappingTarget Tag entity);

    @InheritConfiguration(name = "updateEntityFromDto")
    Tag toEntity(TagUpdateReq dto);

    @Mapping(target = ".", source = "tag.audit")
    TagAdminDetailDTO toAdminDTO(Tag tag);
}
