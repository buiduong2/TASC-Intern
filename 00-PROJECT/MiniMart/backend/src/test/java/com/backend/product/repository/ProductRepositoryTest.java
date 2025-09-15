package com.backend.product.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.backend.common.config.JpaConfig;
import com.backend.inventory.utils.PurchaseOrderConverter;
import com.backend.product.dto.res.ProductDTO;
import com.backend.product.model.ProductStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({ PurchaseOrderConverter.class, JpaConfig.class })
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFindDTOByCategoryIdAndStatus() {
        Page<ProductDTO> page = productRepository.findDTOByCategoryIdAndStatus(1L, ProductStatus.ACTIVE,
                PageRequest.of(0, 10));

        System.out.println(page.getContent().size());
        System.out.println(productRepository.count());
        page.getContent().forEach(System.out::println);
    }
}
