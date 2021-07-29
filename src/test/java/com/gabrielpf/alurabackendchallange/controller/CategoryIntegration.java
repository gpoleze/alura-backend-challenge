package com.gabrielpf.alurabackendchallange.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.gabrielpf.alurabackendchallange.AluraBackendChallangeApplication;

@SpringBootTest(classes = {AluraBackendChallangeApplication.class})
@AutoConfigureMockMvc
@Tag("Integration")
public class CategoryIntegration {

    private final String baseUrl = "/categories";

    @Autowired
    MockMvc mockMvc;

    @Test
    @Tag("Integration")
    void listAllReturn200StatusAndListOfCategoryDto() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        assertFalse(mvcResult.getResponse().getContentAsString().isBlank());
    }
}

