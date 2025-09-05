package com.backend.mock.inventory;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Commit;

import com.backend.inventory.model.Purchase;
import com.backend.inventory.repository.PurchaseItemRepository;
import com.backend.inventory.repository.PurchaseRepository;
import com.backend.mock.JpaAuditTestConfig;
import com.backend.product.model.Product;
import com.backend.product.repository.ProductRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(JpaAuditTestConfig.class)
public class MockPurchaseItem {

    @Autowired
    private PurchaseItemRepository repository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Test
    @Commit
    public void mockPurchaseItem() {
        List<Product> products = productRepository.findAll();
        List<Purchase> purchases = purchaseRepository.findAll();


        
    }

}
