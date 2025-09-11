package com.backend.product.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.backend.common.utils.ToEntity;
import com.backend.product.dto.req.CategoryUpdateReq;
import com.backend.product.dto.res.CategoryAdminDetailDTO;
import com.backend.product.dto.res.CategoryDTO;
import com.backend.product.dto.res.CategoryDetailDTO;
import com.backend.product.model.Category;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    @Mapping(target = "imageUrl", source = "image.url")
    CategoryDTO toDTO(Category category);

    @Mapping(target = "imageUrl", source = "image.url")
    CategoryDetailDTO toDetailDTO(Category category);

    @Mapping(target = "imageUrl", source = "image.url")
    @Mapping(target = ".", source = "audit")
    CategoryAdminDetailDTO toAdminDetailDTO(Category category);

    @ToEntity
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "image", ignore = true)
    Category toEntity(CategoryUpdateReq req);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(@MappingTarget Category category, CategoryUpdateReq req);
}
