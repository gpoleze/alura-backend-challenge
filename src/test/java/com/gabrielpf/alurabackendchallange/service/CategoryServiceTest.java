package com.gabrielpf.alurabackendchallange.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.gabrielpf.alurabackendchallange.dto.CategoryDto;
import com.gabrielpf.alurabackendchallange.model.Category;
import com.gabrielpf.alurabackendchallange.repository.CategoryRepository;


@WebMvcTest(CategoryService.class)
class CategoryServiceTest {

    @Autowired
    private CategoryService service;

    @MockBean
    private CategoryRepository repository;

    private static Category getCategory() {
        return new Category("title", "color");
    }

    private static Category getCategory(String title, String color) {
        return new Category(title, color);
    }

    private static CategoryDto getCategoryDto() {
        return getCategoryDto("title", "color");
    }

    private static CategoryDto getCategoryDto(String title, String color) {
        return new CategoryDto(getCategory(title, color));
    }

    @Nested
    @DisplayName("findAll method")
    class FindAllUnitTest {

        private static Stream<List<Category>> listAllReturnsEmptyListIfNoCategoryExistProvider() {

            List<Category> singleton = Collections.singletonList(getCategory());
            List<Category> categories = Arrays.asList(getCategory("title1", "white"), getCategory("title2", "black"));

            return Stream.of(
                    Collections.EMPTY_LIST,
                    singleton,
                    categories
            );
        }

        @ParameterizedTest
        @MethodSource("listAllReturnsEmptyListIfNoCategoryExistProvider")
        void listAllReturnsEmptyListIfNoCategoryExist(List<Category> categories) throws Exception {
            List<CategoryDto> expected = categories.stream().map(CategoryDto::new).toList();
            doReturn(categories).when(repository).findAll();

            assertEquals(expected, service.findAll());
            verify(repository, times(1)).findAll();
        }
    }
}