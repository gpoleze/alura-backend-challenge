package com.gabrielpf.alurabackendchallange.controller.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.gabrielpf.alurabackendchallange.model.Category;

public record CategoryCreateForm(@NotBlank @Size(max = 256) String title, @NotBlank String color) {
    public Category convert() {
        return new Category(title, color);
    }
}
