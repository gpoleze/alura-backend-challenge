package com.gabrielpf.alurabackendchallange.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.gabrielpf.alurabackendchallange.model.Category;

class CatogoryDtoTest {
    @Test
    void constructorTransformsCategoryInCategoryDTO() {
        Category category = new Category("title", "color");
        CategoryDto categoryDto = new CategoryDto(category);
        assertEquals(category.getId(), categoryDto.id());
        assertEquals(category.getTitle(), categoryDto.title());
        assertEquals(category.getColor(), categoryDto.color());
    }
}