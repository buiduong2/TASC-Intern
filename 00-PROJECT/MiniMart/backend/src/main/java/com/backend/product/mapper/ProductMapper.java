package com.backend.product.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.backend.common.utils.ToEntity;
import com.backend.product.dto.req.ProductUpdateReq;
import com.backend.product.dto.res.ProductDTO;
import com.backend.product.dto.res.ProductDetailDTO;
import com.backend.product.model.Product;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    @Mapping(target = "stock", source = "stock.quantity")
    @Mapping(target = "imageUrl", source = "image.url")
    ProductDTO toDTO(Product product);

    @Mapping(target = "stock", source = "stock.quantity")
    @Mapping(target = "imageUrl", source = "image.url")
    ProductDetailDTO toDetailDTO(Product product);

    @ToEntity
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "stock", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Product toEntity(ProductUpdateReq dto);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(@MappingTarget Product product, ProductUpdateReq dto);
}
