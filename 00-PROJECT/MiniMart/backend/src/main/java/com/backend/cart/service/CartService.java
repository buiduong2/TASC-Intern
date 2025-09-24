package com.backend.cart.service;

import com.backend.cart.dto.req.AddCartItemRequest;
import com.backend.cart.dto.res.CartDTO;

public interface CartService {

    CartDTO getCart(long userId);

    CartDTO addToCart(long userId, AddCartItemRequest req);

    CartDTO updateQuantity(long userId, long itemId, int quantity);

    CartDTO removeItem(long userId, long itemId);

    CartDTO clearCart(long userId);

}
