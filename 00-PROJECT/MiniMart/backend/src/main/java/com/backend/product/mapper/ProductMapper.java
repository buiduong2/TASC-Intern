package com.backend.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

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
}
