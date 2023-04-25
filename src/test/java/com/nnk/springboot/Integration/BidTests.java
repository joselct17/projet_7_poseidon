package com.nnk.springboot.Integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BidTests {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void PostRegistrationForm_shouldSucceedAndRedirected_BidList() throws Exception {
        mockMvc.perform(post("/bidList/validate")
                        .param("account", "account")
                        .param("type", "type")
                        .param("bidQuantity", "bidQuantity")
                        .param("askQuantity", "askQuantity")
                        .param("bid", "bid")
                        .param("ask", "ask")
                        .param("benchmark", "benchmark")
                        .param("bidListDate", "bidListDate")
                        .param("commentary", "commentary")
                        .param("security", "security")
                        .param("status", "status")
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
