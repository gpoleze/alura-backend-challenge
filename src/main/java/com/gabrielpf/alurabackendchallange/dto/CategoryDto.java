package com.gabrielpf.alurabackendchallange.dto;

import java.util.UUID;

import javax.validation.constraints.NotBlank;

import com.gabrielpf.alurabackendchallange.model.Category;

public record CategoryDto(@NotBlank UUID id, @NotBlank String title, @NotBlank String color) {
    public CategoryDto(Category category) {
        this(category.getId(), category.getTitle(), category.getColor());
    }
}
