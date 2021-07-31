package com.gabrielpf.alurabackendchallange.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.gabrielpf.alurabackendchallange.AluraBackendChallangeApplication;
import com.gabrielpf.alurabackendchallange.dto.CategoryDto;
import com.gabrielpf.alurabackendchallange.model.Category;
import com.google.gson.Gson;

@SpringBootTest(classes = {AluraBackendChallangeApplication.class})
@AutoConfigureMockMvc
@Tag("Integration")
public class CategoryIntegration {

    private final String baseUrl = "/categories/";

    @Autowired
    MockMvc mockMvc;

    @Test
    void listAllReturn200StatusAndListOfCategoryDto() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        assertFalse(mvcResult.getResponse().getContentAsString().isBlank());
    }

    @Test
    void getOneReturn200StatusAndGeneralCategoryDto() throws Exception {
        Category general = new Category("GENERAL", "000000");
        general.setId(UUID.fromString("0df591cf-4226-4e3d-a31a-49c8b31e21d0"));

        CategoryDto expected = new CategoryDto(general);

        mockMvc.perform(get(baseUrl + expected.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(new Gson().toJson(expected)))
                .andDo(print());
    }
}

