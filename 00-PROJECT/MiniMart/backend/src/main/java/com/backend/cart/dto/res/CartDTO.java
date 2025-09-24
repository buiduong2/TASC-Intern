package com.backend.cart.dto.res;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDTO {

    private List<CartItemDTO> items;

    private List<AdjustmentCartItem> changes;

    public BigDecimal getSubTotal() {
        if (items == null) {
            return BigDecimal.ZERO;
        }

        return items.stream().map(CartItemDTO::getSubTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static CartDTO createEmptyCart() {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setItems(new ArrayList<>());
        return cartDTO;
    }

    @Getter
    @Setter
    public static class CartItemDTO {

        private long id;

        private int quantity;

        private ProductDTO product;

        private BigDecimal unitPrice;

        public BigDecimal getSubTotal() {
            return this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
    }

    @Getter
    @Setter
    public static class ProductDTO {
        private Long id;
        private String name;

        private String imageUrl;

        private BigDecimal salePrice;
        private BigDecimal compareAtPrice;
    }

    @Getter
    @Setter
    public static class AdjustmentCartItem {
        private CartItemDTO oldItem;
        private int updatedQuantity;
        private BigDecimal updatedPrice;
        private Action action;
    }

    public static enum Action {
        // Đồng bộ nếu số lượng server biến động
        UPDATE,

        // Xóa nếu sản phẩm không còn hợp lệ
        DELETE
    }
}
