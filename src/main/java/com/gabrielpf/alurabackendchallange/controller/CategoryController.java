package com.gabrielpf.alurabackendchallange.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabrielpf.alurabackendchallange.dto.CategoryDto;
import com.gabrielpf.alurabackendchallange.service.CategoryService;

@RestController
@RequestMapping(path = "categories", produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {this.service = service;}

    @GetMapping
    public List<CategoryDto> listAll() {
        return service.findAll();
    }
}
