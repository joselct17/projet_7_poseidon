package com.nnk.springboot.Integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TradeTest {

    @Autowired
    private MockMvc mockMvc;

    @WithMockUser
    @Test
    void PostRegistrationForm_shouldSucceedAndRedirected_Trade() throws Exception {
        mockMvc.perform(post("/trade/validate")
                        .param("account", "account")
                        .param("type", "type")
                        .param("buyQuantity", "buyQuantity")
                        .param("sellQuantity", "sellQuantity")
                        .param("buyPrice", "buyPrice")
                        .param("sellPrice", "sellPrice")
                        .param("benchmark", "benchmark")
                        .param("tradeDate", "tradeDate")
                        .param("security", "security")
                        .param("status", "status")
                        .param("trader", "trader")
                        .param("book", "book")
                        .param("creationName", "creationName")
                        .param("creationDate", "creationDate")
                        .param("revisionName", "revisionName")
                        .param("revisionDate", "revisionDate")
                        .param("dealName", "dealName")
                        .param("dealType", "dealType")
                        .param("sourceListId", "sourceListId")
                        .param("side", "side")
                        .with(csrf()))
                .andExpect(status().isOk())
        ;
    }
}
