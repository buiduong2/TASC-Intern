package com.backend.mock;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;

import com.backend.product.model.Category;
import com.backend.product.model.Product;
import com.backend.product.model.Tag;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.Setter;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class MockProductData {

    @Autowired
    private EntityManager em;

    private MockMapper mapper = Mappers.getMapper(MockMapper.class);

    @Getter
    @Setter
    public static class ProductDTO {
        private String name;
        private String imageUrl;
        private String description;
        private Double salePrice;
        private double compareAtPrice;
        private int stock;
        private List<String> tags;
    }

    // @Test
    @Commit
    public void mockProduct() throws StreamReadException, DatabindException, IOException {
        List<Category> categories = em.createQuery("FROM Category", Category.class).getResultList();

        Category beau = categories.stream().filter(c -> c.getName().equals("Beauty & Healths")).findFirst().get();
        Category breakFast = categories.stream().filter(c -> c.getName().equals("Cooking Essentials")).findFirst()
                .get();
        Category cook = categories.stream().filter(c -> c.getName().equals("Break Fast")).findFirst().get();

        List<Product> products = new ArrayList<>();

        products.addAll(mockList(beau, "./data/beauti-care.json"));
        products.addAll(mockList(breakFast, "./data/break-fast.json"));
        products.addAll(mockList(cook, "./data/Cooking-Essentials.json"));

        List<Tag> tags = mockTags(products);

        Map<String, Tag> mapTagByName = tags.stream()
                .collect(Collectors.toMap(Tag::getName, Function.identity()));

        // Add Product- Tags realtion
        products.forEach(product -> {
            if (product.getTags() != null) {
                Set<Tag> newTags = product
                        .getTags()
                        .stream()
                        .map(t -> mapTagByName.get(t.getName()))
                        .collect(Collectors.toSet());
                product.setTags(newTags);
            }
        });

        // Add Image
        products.forEach(product -> {
            em.persist(product.getImage());
            em.persist(product);
        });

    }

    private List<Tag> mockTags(List<Product> products) {

        Set<String> tagNames = new HashSet<>();
        List<Tag> tags = products.stream()
                .map(Product::getTags)
                .filter(t -> t != null)
                .flatMap(t -> t.stream())
                .filter(t -> {
                    System.out.println(t.getName());
                    if (tagNames.contains(t.getName())) {
                        return false;
                    }
                    tagNames.add(t.getName());
                    return true;
                }).toList();

        for (Tag tag : tags) {
            em.persist(tag);
        }
        return tags;
    }

    private List<Product> mockList(Category category, String fileName)
            throws StreamReadException, DatabindException, IOException {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<ProductDTO> list = om.readValue(
                new File(fileName),
                new TypeReference<List<ProductDTO>>() {
                });

        List<Product> products = list.stream().map(mapper::toEntity)
                .map(p -> {
                    p.setCategory(category);
                    return p;
                }).toList();

        return products;
    }

}
