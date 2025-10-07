package com.product_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.common.mapper.ToEntity;
import com.product_service.dto.req.ProductUpdateReq;
import com.product_service.dto.res.ProductDetailDTO;
import com.product_service.dto.res.ProductSummaryDTO;
import com.product_service.model.Product;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    @Mapping(target = "imageUrl", source = "image.url")
    ProductSummaryDTO toDTO(Product product);

    @Mapping(target = "imageUrl", source = "image.url")
    @Mapping(target = "relates", ignore = true)
    @Mapping(target = "categoryId", source = "category.id")
    ProductDetailDTO toDetailDTO(Product product);

    @ToEntity
    @Mapping(target = "stock", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "image", ignore = true)
    Product toEntity(ProductUpdateReq req);

}
