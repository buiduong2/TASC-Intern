package com.backend.cart.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.cart.dto.req.AddCartItemRequest;
import com.backend.cart.dto.req.UpdateCartItemRequest;
import com.backend.cart.dto.res.CartDTO;
import com.backend.cart.service.CartService;
import com.backend.user.security.CustomUserDetail;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService service;

    @GetMapping
    public CartDTO getCart(@AuthenticationPrincipal CustomUserDetail userDetail) {
        return service.getCart(userDetail.getUserId());
    }

    @PostMapping("/items")
    public CartDTO addToCart(@AuthenticationPrincipal CustomUserDetail userDetail,
            @Valid @RequestBody AddCartItemRequest req) {
        return service.addToCart(userDetail.getUserId(), req);
    }

    @PutMapping("/items/{itemId}")
    public CartDTO updateQuantity(@PathVariable long itemId,
            @Valid @RequestBody UpdateCartItemRequest req,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        return service.updateQuantity(userDetail.getUserId(), itemId, req.getQuantity());
    }

    @DeleteMapping("/items/{itemId}")
    public CartDTO removeFromCart(@PathVariable long itemId, @AuthenticationPrincipal CustomUserDetail userDetail) {
        return service.removeItem(userDetail.getUserId(), itemId);
    }

    @DeleteMapping("/items")
    public CartDTO clearCart(@AuthenticationPrincipal CustomUserDetail userDetail) {
        return service.clearCart(userDetail.getUserId());
    }

}
