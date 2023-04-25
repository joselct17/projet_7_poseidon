package com.nnk.springboot.Integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CurvePointTest {

    @Autowired
    private MockMvc mockMvc;
    @Test
    void PostRegistrationForm_shouldSucceedAndRedirected_CurvePoint() throws Exception {
        mockMvc.perform(post("/curvePoint/validate")
                        .param("curveId", "1")
                        .param("asOfDate", "asOfDate")
                        .param("term", "term")
                        .param("value", "value")
                        .param("creationDate", "creationDate")
                        .with(csrf()))
                .andExpect(status().isOk())
        ;
    }
}
