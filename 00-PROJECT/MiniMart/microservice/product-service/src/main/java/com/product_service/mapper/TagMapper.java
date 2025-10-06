package com.product_service.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.common.mapper.ToEntity;
import com.product_service.dto.req.TagUpdateReq;
import com.product_service.dto.res.TagAdminDetailDTO;
import com.product_service.model.Tag;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TagMapper {

    @ToEntity
    @Mapping(target = "products", ignore = true)
    Tag toEntity(TagUpdateReq req);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(@MappingTarget Tag entity, TagUpdateReq req);

    TagAdminDetailDTO toDetailDTO(Tag tag);
}
