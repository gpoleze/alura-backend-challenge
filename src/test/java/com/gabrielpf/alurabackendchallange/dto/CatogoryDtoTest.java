package com.gabrielpf.alurabackendchallange.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.gabrielpf.alurabackendchallange.model.Category;

class CatogoryDtoTest {
    @Test
    void constructorTransformsCategoryInCategoryDTO() {
        Category category = new Category("title", "color");
        CategoryDto categoryDto = new CategoryDto(category);
        assertEquals(category.getId(), categoryDto.getId());
        assertEquals(category.getTitle(), categoryDto.getTitle());
        assertEquals(category.getColor(), categoryDto.getColor());
    }
}