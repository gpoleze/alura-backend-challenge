package com.gabrielpf.alurabackendchallange.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.gabrielpf.alurabackendchallange.controller.form.CategoryCreateForm;
import com.gabrielpf.alurabackendchallange.dto.CategoryDto;
import com.gabrielpf.alurabackendchallange.exception.DataAlreadyExistsException;
import com.gabrielpf.alurabackendchallange.model.Category;
import com.gabrielpf.alurabackendchallange.service.CategoryService;
import com.google.gson.Gson;


@WebMvcTest(controllers = CategoryController.class)
class CategoriesControllerTest {

    private final String baseUrl = "/categories/";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private CategoryService service;

    private static CategoryDto getCatogoryDto() {
        return getCatogoryDto("title", "color");
    }

    private static CategoryDto getCatogoryDto(String title, String color) {
        return new CategoryDto(new Category(title, color));
    }

    private static String parseToJson(Object o) {
        return new Gson().toJson(o);
    }

    private byte[] getBody() {
        return getBody("nice title", "cool color");
    }

    private byte[] getBody(String title, String color) {
        return parseToJson(new CategoryCreateForm(title, color)).getBytes();
    }

    @Nested
    @DisplayName("findAll method")
    class FindAllUnitTest {

        private static Stream<Arguments> listAllReturnsEmptyListIfNoCategoryExistProvider() {

            List<CategoryDto> singleton = Collections.singletonList(getCatogoryDto());
            List<CategoryDto> categoryDtos = Arrays.asList(getCatogoryDto("title1", "white"), getCatogoryDto("title2", "black"));
            return Stream.of(
                    Arguments.of(Collections.EMPTY_LIST, "[]"),
                    Arguments.of(singleton, parseToJson(singleton)),
                    Arguments.of(categoryDtos, parseToJson(categoryDtos))
            );
        }

        @ParameterizedTest
        @MethodSource("listAllReturnsEmptyListIfNoCategoryExistProvider")
        void listAllReturnsEmptyListIfNoCategoryExist(List<CategoryDto> categoriesDto, String jsonResult) throws Exception {
            doReturn(categoriesDto).when(service).findAll();

            mockMvc.perform(get(baseUrl))
                    .andExpect(status().isOk())
                    .andExpect(content().json(jsonResult));
        }


//
//        @Test
//        void listAllRestunsListWithMoreThanOneElementWhenMoreThanOneCategoryExist() {
//            Assertions.fail();
//        }
    }

    @Nested
    @DisplayName("getOne one")
    class GetOneUnitTest {
        @Test
        void succeedToGetOneCategoriesWhenIdExists() throws Exception {
            final var categoryDto = getCatogoryDto();

            when(service.findById(categoryDto.id()))
                    .thenReturn(Optional.of(categoryDto));

            mockMvc
                    .perform(get(baseUrl + categoryDto.id()))
                    .andExpect(status().isOk())
                    .andExpect(content().json(parseToJson(categoryDto)))
                    .andDo(print());
        }

        @Test
        void failsToGetOneCategoriesWhenIdDoesNotExists() throws Exception {
            final var categoryDto = getCatogoryDto();

            when(service.findById(categoryDto.id()))
                    .thenReturn(Optional.of(categoryDto));
            var badId = UUID.randomUUID();

            mockMvc
                    .perform(get(baseUrl + badId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Not Found"));
        }

        @Test
        void failsToGetOneCategoriesWhenIdIsNotValid() throws Exception {
            final var categoryDto = getCatogoryDto();

            when(service.findById(categoryDto.id()))
                    .thenReturn(Optional.of(categoryDto));

            mockMvc
                    .perform(get(baseUrl + "not-valid-uuid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("create method")
    class CreateCategory {
        @Test
        void categoryIsSavedWhenFormIsValid() throws Exception {
            final var categoryDto = getCatogoryDto();

            when(service.save(any())).thenReturn(categoryDto);

            MockHttpServletRequestBuilder request = post(baseUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getBody());

            mockMvc
                    .perform(request)
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(content().json(parseToJson(categoryDto)))
                    .andExpect(redirectedUrl("http://localhost" + baseUrl + categoryDto.id()));
        }

        private static Stream<Arguments> providerReturnBadRequestWhenMissingArgument() {

            String title = "title";
            String color = "color";
            return Stream.of(
                    Arguments.of(title, color),
                    Arguments.of(title, null),
                    Arguments.of(null, color),
                    Arguments.of(null, null)
            );
        }

        @ParameterizedTest
        @MethodSource("providerReturnBadRequestWhenMissingArgument")
        void returnBadRequestWhenMissingArgument(String title, String color) throws Exception {

            MockHttpServletRequestBuilder request = post(baseUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(parseToJson(getBody(title, color)));

            mockMvc
                    .perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(service, times(0)).save(any());
        }

        @Test
        void returnBadRequestWhenBodyArgumentsAreValidButTitleAlreadyExists() throws Exception {
            when(service.save(any(CategoryCreateForm.class))).thenThrow(new DataAlreadyExistsException(CategoryCreateForm.class, "title"));

            MockHttpServletRequestBuilder request = post(baseUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(getBody());


            mockMvc.perform(request)
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json("{\"field\":\"title\", \"error\":\"The given value is already present in the database\"}"))
                    .andDo(print());
        }
    }
}