package com.gabrielpf.alurabackendchallange.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabrielpf.alurabackendchallange.dto.CategoryDto;
import com.gabrielpf.alurabackendchallange.dto.VideoDto;
import com.gabrielpf.alurabackendchallange.model.Category;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("Integration")
public class CategoryIntegration {

    private final String baseUrl = "/categories/";
    private final Gson gson = new Gson();
    private final String generalCategoryId = "0df591cf-4226-4e3d-a31a-49c8b31e21d0";

    @Autowired
    MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    private CategoryDto createCategory() throws Exception {
        String expectedTitle = "I should be deleted - integration test" + UUID.randomUUID();
        String expectedColor = "FFF";

        Map<String, String> content = new HashMap<>();
        content.put("title", expectedTitle);
        content.put("color", expectedColor);
        var body = gson.toJson(content).getBytes();

        // Create category bo be deleted
        var creationResponse = mockMvc.perform(post(baseUrl).content(body).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        var id = JsonParser.parseString(creationResponse.getContentAsString()).getAsJsonObject().get("id").getAsString();
        var title = JsonParser.parseString(creationResponse.getContentAsString()).getAsJsonObject().get("title").getAsString();
        var color = JsonParser.parseString(creationResponse.getContentAsString()).getAsJsonObject().get("color").getAsString();

        return new CategoryDto(UUID.fromString(id), title, color);
    }

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
        general.setId(UUID.fromString(generalCategoryId));

        CategoryDto expected = new CategoryDto(general);

        mockMvc.perform(get(baseUrl + expected.id()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(gson.toJson(expected)))
                .andDo(print());
    }

    @Test
    void get201StatusAndBodyContentWhenCreatingANewCategory() throws Exception {
        String expectedTitle = "title from integration test" + UUID.randomUUID();
        String expectedColor = "FFF";

        Map<String, String> content = new HashMap<>();
        content.put("title", expectedTitle);
        content.put("color", expectedColor);
        var body = gson.toJson(content).getBytes();

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

    @Test
    void get204StatusAndNoBodyContentWhenDeletingCategory() throws Exception {
        CategoryDto categoryDto = createCategory();

        mockMvc.perform(delete(baseUrl + categoryDto.id()))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void receive200StatusAndFullCategoryBackWhenUpdatingACategoryTitle() throws Exception {
        CategoryDto categoryDto = createCategory();
        var newTitle = "updated title";
        CategoryDto updatedCategory = new CategoryDto(categoryDto.id(), newTitle, categoryDto.color());

        mockMvc.perform(patch(baseUrl + categoryDto.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + newTitle + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(updatedCategory)));
    }

    @Test
    void receive200StatusAndAListOfVideosWhenListingByCategory() throws Exception {
        mockMvc.perform(get(baseUrl + generalCategoryId + "/videos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertNotNull(objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<VideoDto>>() {})))
                .andExpect(result -> assertFalse(objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<VideoDto>>() {}).isEmpty()));
    }
}

