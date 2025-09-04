package com.backend.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.backend.product.dto.res.CategoryDTO;
import com.backend.product.dto.res.CategoryDetailDTO;
import com.backend.product.model.Category;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    @Mapping(target = "imageUrl", source = "image.src")
    CategoryDTO toDTO(Category category);

    @Mapping(target = "imageUrl", source = "image.src")
    CategoryDetailDTO toDetailDTO(Category category);
}
