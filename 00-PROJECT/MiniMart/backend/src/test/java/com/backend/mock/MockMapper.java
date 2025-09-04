package com.backend.mock;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.mock.MockProductData.ProductDTO;
import com.backend.product.model.Product;
import com.backend.product.model.ProductImage;
import com.backend.product.model.Tag;

@Mapper
public interface MockMapper {

    @Mapping(target = "stock", ignore = true)
    @Mapping(target = "tags", expression = "java( toTags(dto.getTags()) )")
    @Mapping(target = "status", expression = "java(com.backend.product.model.Status.ACTIVE)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", expression = "java( toImage(dto.getImageUrl()) )")
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductDTO dto);

    default ProductImage toImage(String imageUrl) {
        ProductImage image = new ProductImage();
        image.setSrc(imageUrl);
        return image;
    }

    default Set<Tag> toTags(List<String> tags) {
        return tags.stream().map(s -> {
            Tag tag = new Tag();
            tag.setName(s);
            return tag;
        }).collect(Collectors.toSet());
    }
}
