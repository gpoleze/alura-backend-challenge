package com.gabrielpf.alurabackendchallange.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "category")
public class Category extends EntityWithUuidId {
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String color;

    protected Category() {}

    public Category(String title, String color) {
        this.title = title;
        this.color = color;
    }

    public Category(Category other) {
        super(other.getId());
        this.title = other.title;
        this.color = other.color;
    }

    public String getTitle() {
        return title;
    }

    public String getColor() {
        return color;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
