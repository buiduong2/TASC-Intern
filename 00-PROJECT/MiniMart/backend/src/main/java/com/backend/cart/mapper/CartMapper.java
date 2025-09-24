package com.backend.cart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.backend.cart.dto.res.CartDTO;
import com.backend.cart.dto.res.CartDTO.CartItemDTO;
import com.backend.cart.dto.res.CartDTO.ProductDTO;
import com.backend.cart.model.Cart;
import com.backend.cart.model.CartItem;
import com.backend.product.model.Product;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartMapper {

    @Mapping(target = "changes", ignore = true)
    CartDTO toDTO(Cart cart);

    CartItemDTO toItemDTO(CartItem cartItem);

    @Mapping(target = "imageUrl", source = "image.url")
    ProductDTO toProductDTO(Product product);

}
