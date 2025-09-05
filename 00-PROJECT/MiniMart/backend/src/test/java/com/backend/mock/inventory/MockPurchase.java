package com.backend.mock.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Commit;

import com.backend.common.model.Audit;
import com.backend.inventory.model.Purchase;
import com.backend.inventory.repository.PurchaseRepository;
import com.backend.mock.JpaAuditTestConfig;
import com.github.javafaker.Faker;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(JpaAuditTestConfig.class)
public class MockPurchase {

    @Autowired
    private PurchaseRepository repository;

    @Test
    @Commit
    public void mockEntity() {

        Faker faker = new Faker();
        for (int i = 0; i < 20; i++) {
            Purchase purchase = new Purchase();
            purchase.setSupplier(faker.company().name());
            Audit audit = new Audit();
            purchase.setAudit(audit);
            repository.save(purchase);

        }
    }

}
