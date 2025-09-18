package com.backend.inventory.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import com.backend.common.config.JpaConfig;
import com.backend.inventory.dto.req.StockAllocationFilter;
import com.backend.inventory.service.StockAllocationService;
import com.backend.inventory.utils.PurchaseOrderConverter;
import com.backend.inventory.utils.StockAllocationSpecs;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({ PurchaseOrderConverter.class, JpaConfig.class, StockAllocationServiceImpl.class, StockAllocationSpecs.class })
public class StockAllocationServiceImplTest {

    @Autowired
    private StockAllocationService service;

    @Test
    void testFindAll() {
        service.findAll(new StockAllocationFilter(), PageRequest.ofSize(5));
    }
}
