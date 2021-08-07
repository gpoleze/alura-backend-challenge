package com.gabrielpf.alurabackendchallange.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabrielpf.alurabackendchallange.controller.form.CategoryCreateForm;
import com.gabrielpf.alurabackendchallange.controller.form.CategoryUpdateForm;
import com.gabrielpf.alurabackendchallange.dto.CategoryDto;
import com.gabrielpf.alurabackendchallange.dto.VideoCategoryDto;
import com.gabrielpf.alurabackendchallange.exception.DataAlreadyExistsException;
import com.gabrielpf.alurabackendchallange.exception.EntityCannotBeDeletedException;
import com.gabrielpf.alurabackendchallange.model.Category;
import com.gabrielpf.alurabackendchallange.service.CategoryService;
import com.google.gson.Gson;


@WebMvcTest(controllers = CategoryController.class)
class CategoriesControllerTest {

    private final String baseUrl = "/categories/";
    private static final Gson gson = new Gson();
    private ObjectMapper objectMapper = new ObjectMapper();

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
        return gson.toJson(o);
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

    @Nested
    @DisplayName("Delete category")
    class DeleteCategoryUnitTest {
        @Test
        void deleteCategoryWhenItemExistsInTheDatabase() throws Exception {
            CategoryDto categoryDto = getCatogoryDto();

            doNothing().when(service).delete(categoryDto.id());

            mockMvc.perform(delete(baseUrl + categoryDto.id()))
                    .andExpect(status().isNoContent());
        }

        @Test
        void returnASuccessStatusWhenTryingToDeleteAnItemThatDoesNotExistInTheDatabase() throws Exception {
            doNothing().when(service).delete(any());

            mockMvc.perform(delete(baseUrl + UUID.randomUUID()))
                    .andExpect(status().isNoContent());
        }

        @Test
        void sendBadRequestResponseWhenIdIsInvalidAndTryingToDeleteAVideo() throws Exception {
            mockMvc.perform(delete(baseUrl + "i-am-clearly-not-a-valid-uuid"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void returnBadRequestWhenTryingToDeleteACategotrieThatHasVideoAssociatedWithIt() throws Exception {
            var id = UUID.randomUUID();

            doThrow(new EntityCannotBeDeletedException(VideoCategoryDto.class, "id")).when(service).delete(id);

            mockMvc.perform(delete(baseUrl + id))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityCannotBeDeletedException));
        }
    }

    @Nested
    @DisplayName("Update category")
    class UpdateCategoryUnitTest {
        private static Stream<Arguments> returnUpdatedCategoryWhenRequestIsCorrectProvider() {
            Function<String[][], Map<String, String>> getMap = stringArray -> Stream.of(stringArray).collect(Collectors.toMap(data -> data[0], data -> data[1]));
            return Stream.of(
                    Arguments.of(getMap.apply(new String[][]{{"title", "newTitle"}})),
                    Arguments.of(getMap.apply(new String[][]{{"color", "new color"}})),
                    Arguments.of(getMap.apply(new String[][]{{"title", "newTitle"}, {"color", "new color"}}))
            );
        }

        @ParameterizedTest
        @MethodSource("returnUpdatedCategoryWhenRequestIsCorrectProvider")
        void returnUpdatedCategoryWhenRequestIsCorrect(Map<String, String> items) throws Exception {
            final var body = gson.toJson(items);
            final CategoryUpdateForm payload = objectMapper.readValue(body, CategoryUpdateForm.class);
            final var title = payload.title() != null ? payload.title() : "old title";
            final var color = payload.color() != null ? payload.color() : "old color";
            final var categoryDto = getCatogoryDto(title, color);

            doReturn(Optional.of(categoryDto))
                    .when(service)
                    .update(eq(categoryDto.id()), any(CategoryUpdateForm.class));

            var response = mockMvc.perform(
                            patch(baseUrl + categoryDto.id()).contentType(MediaType.APPLICATION_JSON).content(body)
                    )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            CategoryDto responseCategory = objectMapper.readValue(response, CategoryDto.class);

            assertNotNull(responseCategory.title());
            assertNotNull(responseCategory.color());

            assumingThat(payload.title() != null, () -> assertEquals(payload.title(), responseCategory.title()));
            assumingThat(payload.color() != null, () -> assertEquals(payload.color(), responseCategory.color()));
        }

        @Test
        void returnVideoWithoutAnyChangesWhenTryingToUpdateVideoAndTheGivenIdExistsButBodyIsEmpty() throws Exception {
            CategoryDto categoryDto = getCatogoryDto();
            doReturn(Optional.of(categoryDto)).when(service).findById(categoryDto.id());

            mockMvc.perform(
                            patch(baseUrl + categoryDto.id()).contentType(MediaType.APPLICATION_JSON).content("{}")
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().json(gson.toJson(categoryDto)));
        }

        @Test
        void returnBadRequestWhenUpdatingVideoAndIdISNotValid() throws Exception {
            mockMvc.perform(
                            patch(baseUrl + "definitly-not-valid-uuid").contentType(MediaType.APPLICATION_JSON).content("{}")
                    )
                    .andExpect(status().isBadRequest());
        }

        @Test
        void returnBadRequestUpdatingVideoAndTheGivenIdDoesntExist() throws Exception {
            doReturn(Optional.empty()).when(service).update(UUID.randomUUID(), new CategoryUpdateForm(null, null));
            mockMvc.perform(patch(baseUrl + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isNotFound());
        }

    }

}