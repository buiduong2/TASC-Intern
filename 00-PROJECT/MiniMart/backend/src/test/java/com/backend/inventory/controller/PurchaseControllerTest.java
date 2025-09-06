package com.backend.inventory.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.backend.common.config.JpaConfig;
import com.backend.inventory.dto.res.PurchaseDTO;
import com.backend.inventory.service.PurchaseService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = PurchaseController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = JpaConfig.class))
@SuppressWarnings("removal")
public class PurchaseControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private PurchaseService purchaseService;

    @Test
    void testCreate_whenInvalid_thenReturn400() throws Exception {

        MvcResult result = mvc.perform(post("/api/purchases")
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['status']", Matchers.is(400)))
                .andExpect(jsonPath("$['error']", Matchers.is("Validation Error")))
                .andExpect(jsonPath("$['message']", Matchers.is("Input validation Failed")))
                .andExpect(jsonPath("$['errors']", Matchers.iterableWithSize(Matchers.greaterThan(1))))
                .andReturn();

        System.out.println(prettyJson(result.getResponse().getContentAsString()));

    }

    @Test
    void testCreate_whenValid_thenReturn200() throws Exception {

        String json = """
                {
                    "supplier": "ABC Corp",
                    "items": [
                        {
                        "productId": 12345,
                        "quantity": 10,
                        "costPrice": 25000
                        },
                        {
                        "productId": 67890,
                        "quantity": 5,
                        "costPrice": 14500
                        }
                    ]
                }
                """;

        when(purchaseService.create(Mockito.any())).thenReturn(new PurchaseDTO(0, null, null, 0, 0));

        MvcResult result = mvc.perform(post("/api/purchases")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        verify(purchaseService, times(1)).create(Mockito.any());

        System.out.println(prettyJson(result.getResponse().getContentAsString()));
    }

    private static String prettyJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object obj = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return json;
        }
    }
}
