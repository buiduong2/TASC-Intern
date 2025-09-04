package com.backend.product.model;

import java.util.Objects;
import java.util.Set;

import org.hibernate.proxy.HibernateProxy;

import com.backend.common.model.Audit;
import com.backend.inventory.model.Stock;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@NamedEntityGraph(name = "Product.ProductDTO", attributeNodes = {
        @NamedAttributeNode(value = "image"),
        @NamedAttributeNode(value = "stock"),
})

@NamedEntityGraph(name = "Product.ProductDetailDTO", attributeNodes = {
        @NamedAttributeNode(value = "image"),
        @NamedAttributeNode(value = "stock"),
        @NamedAttributeNode(value = "tags")
})

@Entity
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double salePrice;

    private double compareAtPrice;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Embedded
    private Audit audit;

    @OneToOne(fetch = FetchType.LAZY)
    private Stock stock;

    @OneToOne(fetch = FetchType.LAZY)
    private ProductImage image;

    @ManyToMany
    private Set<Tag> tags;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass)
            return false;
        Product product = (Product) o;
        return getId() != null && Objects.equals(getId(), product.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
