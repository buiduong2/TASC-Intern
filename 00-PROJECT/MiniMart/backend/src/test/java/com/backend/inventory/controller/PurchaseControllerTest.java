package com.backend.inventory.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.backend.common.config.JpaConfig;
import com.backend.inventory.dto.req.CreatePurchaseReq;
import com.backend.inventory.dto.req.PurchaseItemReq;
import com.backend.inventory.dto.res.PurchaseDTO;
import com.backend.inventory.service.PurchaseService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = PurchaseController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = JpaConfig.class))
@SuppressWarnings("removal")
public class PurchaseControllerTest {

    private static final String API_PURCHASES = "/api/purchases";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PurchaseService purchaseService;

    private CreatePurchaseReq req;

    @BeforeEach
    void setup() {
        PurchaseItemReq item1 = new PurchaseItemReq();
        item1.setProductId(1L);
        item1.setQuantity(10);
        item1.setCostPrice(1000.0);

        PurchaseItemReq item2 = new PurchaseItemReq();
        item2.setProductId(2L);
        item2.setQuantity(5);
        item2.setCostPrice(2000.0);

        req = new CreatePurchaseReq();
        req.setSupplier("ABC Corp");
        req.setItems(List.of(item1, item2));
    }

    private void testValidate(CreatePurchaseReq purchaseItemReq, int status) throws Exception {
        ObjectMapper om = new ObjectMapper();
        String content = om.writeValueAsString(purchaseItemReq);
        when(purchaseService.create(purchaseItemReq))
                .thenReturn(new PurchaseDTO());
        mvc.perform(post(API_PURCHASES)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(status));

        if (status >= 200 && status < 300) {
            verify(purchaseService, times(1)).create(Mockito.any());
        } else {
            verify(purchaseService, times(0)).create(Mockito.any());

        }
    }

    /**
     * Valid => 201
     */
    @Test
    void createPurchase_withValidRequest_shouldReturnCreated() throws Exception {
        testValidate(req, 201);
    }

    @Test
    void testCreate_whenInvalid_thenReturn400() throws Exception {

        mvc.perform(post(API_PURCHASES)
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['status']", Matchers.is(400)))
                .andExpect(jsonPath("$['error']", Matchers.is("Validation Error")))
                .andExpect(jsonPath("$['message']", Matchers.is("Input validation Failed")))
                .andExpect(jsonPath("$['errors']", Matchers.iterableWithSize(Matchers.greaterThan(1))));

        // System.out.println(prettyJson(result.getResponse().getContentAsString()));
    }

    /**
     * Nếu supplier == Null || empty => 400
     */
    @Test
    void createPurchase_withoutSupplier_shouldReturnBadRequest() throws Exception {
        req.setSupplier(null);
        testValidate(req, 400);
        req.setSupplier("  ");
        testValidate(req, 400);
    }

    /**
     * Nếu PurchaseItem == NULL || EMpty => 400 || PurchaseItem = {}
     */
    @Test
    void createPurchase_withEmptyItems_shouldReturnBadRequest() throws Exception {
        req.setItems(List.of(new PurchaseItemReq()));
        testValidate(req, 400);
        req.setItems(null);
        testValidate(req, 400);
        req.setItems(Collections.emptyList());
        testValidate(req, 400);
    }

    /**
     * PurrchaseItem.productId > 0
     */
    @Test
    void createPurchase_withInvalidProductId_shouldReturnBadRequest() throws Exception {
        req.getItems().get(0).setProductId(null);
        testValidate(req, 400);

        req.getItems().get(0).setProductId(-1L);
        testValidate(req, 400);
    }

    /**
     * quantity > 0
     * 
     */
    @Test
    void createPurchaseItem_withZeroQuantity_shouldReturnBadRequest() throws Exception {
        req.getItems().get(0).setQuantity(0);
        testValidate(req, 400);
    }

    /**
     * Cosst_price > 0
     */
    @Test
    void createPurchaseItem_withNegativeCostPrice_shouldReturnBadRequest() throws Exception {

        req.getItems().get(0).setCostPrice(-1d);
        testValidate(req, 400);

        req.getItems().get(0).setCostPrice(0);
        testValidate(req, 400);
    }

    @Test
    void createPurchase_withValidJson_shouldReturnCreated() throws Exception {

        String json = """
                {
                    "supplier": "ABC Corp",
                    "items": [
                        { "productId": 12345, "quantity": 10, "costPrice": 25000 },
                        { "productId": 67890, "quantity": 5, "costPrice": 14500 }
                    ]
                }
                """;

        PurchaseDTO fakeResponse = new PurchaseDTO(
                1L,
                LocalDateTime.of(2025, 9, 7, 10, 0), // giả lập createdAt
                "ABC Corp",
                15, // totalQuantity = 10 + 5
                39500.0 // totalCostPrice = 25000 + 14500
        );
        when(purchaseService.create(Mockito.any())).thenReturn(fakeResponse);

        mvc.perform(post(API_PURCHASES)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.supplier").value("ABC Corp"))
                .andExpect(jsonPath("$.totalQuantity").value(15))
                .andExpect(jsonPath("$.totalCostPrice").value(39500.0))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        verify(purchaseService, times(1)).create(Mockito.any());

    }

}
