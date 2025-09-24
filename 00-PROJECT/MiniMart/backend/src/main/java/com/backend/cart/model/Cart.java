package com.backend.cart.model;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.backend.user.model.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@NamedEntityGraph(name = Cart.NamedGraph_DTO, attributeNodes = {
        @NamedAttributeNode(value = "items", subgraph = "items-with-products")
}, subgraphs = {
        @NamedSubgraph(name = "items-with-products", attributeNodes = {
                @NamedAttributeNode(value = "product", subgraph = "product-with-images")
        }),
        @NamedSubgraph(name = "product-with-images", attributeNodes = {
                @NamedAttributeNode("image"),
                @NamedAttributeNode("stock")
        })
})

@NamedEntityGraph(name = Cart.NamedGraph_Item, attributeNodes = {
        @NamedAttributeNode(value = "items")
})

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Cart {

    public static final String NamedGraph_DTO = "Cart.CartDTO";
    public static final String NamedGraph_Item = "Cart.CartItem";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> items = new LinkedHashSet<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void addItem(CartItem cartItem) {
        this.items.add(cartItem);
        cartItem.setCart(this);
    }

    public void removeItem(CartItem cartItem) {
        this.items.remove(cartItem);
        cartItem.setCart(null);
    }

    public void clearItem() {
        this.items.forEach(item -> item.setCart(null));
        this.items.clear();
    }
}