package com.gabrielpf.alurabackendchallange.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.gabrielpf.alurabackendchallange.dto.CategoryDto;
import com.gabrielpf.alurabackendchallange.model.Category;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

//@SpringBootTest(classes = {AluraBackendChallangeApplication.class})
@SpringBootTest
@AutoConfigureMockMvc
@Tag("Integration")
public class CategoryIntegration {

    private final String baseUrl = "/categories/";
    private final Gson gson = new Gson();

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

        mockMvc.perform(get(baseUrl + expected.id()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(gson.toJson(expected)))
                .andDo(print());
    }

    @Test
    void get201StatusAndBodyContentWhenCreatingANewCategory() throws Exception {
        String expectedTitle = "title from integration test" + UUID.randomUUID().toString();
        String expectedColor = "FFF";

        Map<String, String> content = new HashMap<>();
        content.put("title", expectedTitle);
        content.put("color", expectedColor);
        var body = gson.toJson(content).getBytes();

        CategoryDto expected = new CategoryDto(UUID.randomUUID(), expectedTitle, expectedColor);

        MockHttpServletResponse response = mockMvc.perform(post(baseUrl)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn().getResponse();

        var actual = JsonParser.parseString(response.getContentAsString()).getAsJsonObject();

        assertEquals(expectedTitle, actual.get("title").getAsString());
        assertEquals(expectedColor, actual.get("color").getAsString());
        assertFalse(actual.get("id").getAsString().isEmpty());

        assertNotNull(response.getRedirectedUrl());
        assertTrue(response.getRedirectedUrl().startsWith("http://localhost" + baseUrl));
    }
}

