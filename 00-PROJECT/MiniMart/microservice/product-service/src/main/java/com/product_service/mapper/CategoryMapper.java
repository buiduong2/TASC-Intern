package com.product_service.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.common.mapper.ToEntity;
import com.product_service.dto.req.CategoryUpdateReq;
import com.product_service.dto.res.CategoryAdminDetailDTO;
import com.product_service.model.Category;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    @ToEntity
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "image", ignore = true)
    Category toEntity(CategoryUpdateReq req);

    @InheritConfiguration(name = "toEntity")
    void update(@MappingTarget Category entity, CategoryUpdateReq req);

    @Mapping(target = "imageUrl", source = "image.url")
    CategoryAdminDetailDTO toDTO(Category entity);
}
