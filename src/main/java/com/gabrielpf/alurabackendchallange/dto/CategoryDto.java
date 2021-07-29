package com.gabrielpf.alurabackendchallange.dto;

import java.util.Objects;
import java.util.UUID;

import com.gabrielpf.alurabackendchallange.model.Category;

public class CategoryDto {
    private final UUID id;
    private final String title;
    private final String color;

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.title = category.getTitle();
        this.color = category.getColor();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryDto that = (CategoryDto) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, color);
    }
}
