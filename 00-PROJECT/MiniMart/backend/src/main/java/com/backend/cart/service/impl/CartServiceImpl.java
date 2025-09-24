package com.backend.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.cart.dto.req.AddCartItemRequest;
import com.backend.cart.dto.res.CartDTO;
import com.backend.cart.dto.res.CartDTO.Action;
import com.backend.cart.dto.res.CartDTO.AdjustmentCartItem;
import com.backend.cart.dto.res.CartDTO.CartItemDTO;
import com.backend.cart.mapper.CartMapper;
import com.backend.cart.model.Cart;
import com.backend.cart.model.CartItem;
import com.backend.cart.repository.CartItemRepository;
import com.backend.cart.repository.CartRepository;
import com.backend.cart.service.CartService;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.utils.ErrorCode;
import com.backend.order.exception.NotEnoughStockException;
import com.backend.product.model.Product;
import com.backend.product.model.ProductStatus;
import com.backend.product.repository.ProductRepository;
import com.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository repository;

    private final UserRepository userRepository;

    private final CartItemRepository itemRepository;

    private final ProductRepository productRepository;

    private final CartMapper mapper;

    @Override
    public CartDTO getCart(long userId) {
        return repository.findByUserIdForDTO(userId)
                .map(this::syncCart)
                .orElseGet(CartDTO::createEmptyCart);
    }

    @Transactional
    @Override
    public CartDTO addToCart(long userId, AddCartItemRequest req) {

        Product product = productRepository.findDTOByIdAndStatus(req.getProductId(), ProductStatus.ACTIVE)
                .orElseThrow(
                        () -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND.format(req.getProductId())));

        Cart cart = repository.findByUserIdForDTO(userId)
                .orElseGet(() -> create(userId));

        CartItem cartItem = itemRepository.findByUserIdAndProductId(userId, req.getProductId())
                .orElseGet(() -> createItem(cart, product));

        if (cartItem.getQuantity() + req.getQuantity() > product.getStock().getQuantity()) {
            throw NotEnoughStockException.builder()
                    .message("Product not enough stock")
                    .productId(product.getId())
                    .availableQuantity(product.getStock().getQuantity())
                    .requestedQuantity(req.getQuantity())
                    .build();
        }

        cartItem.setQuantity(cartItem.getQuantity() + req.getQuantity());
        return syncCart(cart);
    }

    private Cart create(long userId) {

        Cart cart = new Cart();

        cart.setUser(userRepository.getReferenceById(userId));

        repository.save(cart);
        return cart;
    }

    private CartItem createItem(Cart cart, Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setUnitPrice(product.getSalePrice() != null ? product.getSalePrice() : product.getCompareAtPrice());
        cart.addItem(cartItem);
        return itemRepository.save(cartItem);
    }

    private CartDTO syncCart(Cart cart) {

        List<AdjustmentCartItem> changes = new ArrayList<>();
        List<CartItem> itemToRemoves = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int availableQuantity = product.getStock() == null ? 0 : product.getStock().getQuantity();
            int currentQuantity = cartItem.getQuantity();
            ProductStatus status = product.getStatus();

            BigDecimal currentPrice = cartItem.getUnitPrice();
            BigDecimal actualPrice = product.getSalePrice() == null ? product.getCompareAtPrice()
                    : product.getSalePrice();

            if (status != ProductStatus.ACTIVE || availableQuantity == 0 || currentQuantity == 0) {
                AdjustmentCartItem change = new AdjustmentCartItem();
                CartItemDTO dto = mapper.toItemDTO(cartItem);
                change.setOldItem(dto);
                change.setAction(Action.DELETE);
                change.setUpdatedQuantity(0);
                changes.add(change);

                // Update Entity
                itemToRemoves.add(cartItem);

                continue;
            }

            AdjustmentCartItem change = null;

            boolean needUpdateQty = currentQuantity > availableQuantity;
            boolean needUpdatePrice = currentPrice.compareTo(actualPrice) != 0;

            if (needUpdatePrice || needUpdateQty) {
                change = new AdjustmentCartItem();
                CartItemDTO dto = mapper.toItemDTO(cartItem);
                change.setOldItem(dto);
                change.setAction(Action.UPDATE);
                changes.add(change);

                if (needUpdateQty) {
                    change.setUpdatedQuantity(availableQuantity);
                    cartItem.setQuantity(availableQuantity);// update Entity
                }

                if (needUpdatePrice) {
                    change.setUpdatedPrice(actualPrice);
                    cartItem.setUnitPrice(actualPrice);// update Entity
                }

            }

        }

        for (CartItem cartItem : itemToRemoves) {
            cart.removeItem(cartItem);
        }

        CartDTO dto = mapper.toDTO(cart);
        dto.setChanges(changes);

        return dto;
    }

    @Transactional
    @Override
    public CartDTO updateQuantity(long userId, long itemId, int requestedQuantity) {
        Cart cart = repository.findByUserIdForDTO(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CART_NOT_FOUND.format(userId)));

        CartItem item = cart.getItems().stream().filter(i -> i.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CART_ITEM_NOT_FOUND.format(itemId)));

        Product product = item.getProduct();
        int availableQuantity = product.getStock().getQuantity();

        if (requestedQuantity > availableQuantity) {
            throw NotEnoughStockException.builder()
                    .message("Product not enough stock")
                    .productId(product.getId())
                    .availableQuantity(product.getStock().getQuantity())
                    .requestedQuantity(requestedQuantity)
                    .build();
        }

        item.setQuantity(requestedQuantity);

        return syncCart(cart);

    }

    @Transactional
    @Override
    public CartDTO removeItem(long userId, long itemId) {
        Cart cart = repository.findByUserIdForDTO(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CART_NOT_FOUND.format(userId)));

        CartItem item = cart.getItems().stream().filter(i -> i.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CART_ITEM_NOT_FOUND.format(itemId)));

        cart.removeItem(item);
        return syncCart(cart);
    }

    @Transactional
    @Override
    public CartDTO clearCart(long userId) {
        Cart cart = repository.findByUserIdForClearItem(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CART_NOT_FOUND.format(userId)));

        cart.clearItem();

        return syncCart(cart);
    }

}
