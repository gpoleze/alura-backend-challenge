package com.gabrielpf.alurabackendchallange.controller.form;

import java.lang.reflect.Field;
import java.util.Arrays;

import javax.validation.constraints.Size;

import com.gabrielpf.alurabackendchallange.dto.CategoryDto;
import com.gabrielpf.alurabackendchallange.model.Category;

public record CategoryUpdateForm(@Size(max = 256) String title, String color) implements UpdateForm<Category> {
    @Override
    public Category update(Category category) {
        Category updated = new Category(category);
        if (title != null && !title.isBlank())
            updated.setTitle(title);
        if (color != null && !color.isBlank())
            updated.setColor(color);
        return updated;
    }
}
