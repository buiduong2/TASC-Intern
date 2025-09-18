package com.backend.product.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.backend.common.model.Audit;
import com.backend.common.model.GetIdAble;
import com.backend.inventory.model.Stock;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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

@NamedEntityGraph(name = Product.NamedGraph_DTO, attributeNodes = {
        @NamedAttributeNode(value = "image"),
        @NamedAttributeNode(value = "stock"),
})

@NamedEntityGraph(name = Product.NamedGraph_DetailDTO, attributeNodes = {
        @NamedAttributeNode(value = "image"),
        @NamedAttributeNode(value = "stock"),
        @NamedAttributeNode(value = "tags")
})

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Product implements GetIdAble<Long> {

    public static final String NamedGraph_DTO = "Product.ProductDTO";
    public static final String NamedGraph_DetailDTO = "Product.ProductDetailDTO";

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double salePrice;

    private double compareAtPrice;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Embedded
    private Audit audit = new Audit();

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "product")
    private Stock stock;

    @OneToOne(fetch = FetchType.LAZY)
    private ProductImage image;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }

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
        if (this instanceof HibernateProxy) {
            return ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode();
        } else {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            return result;
        }
    }

}
