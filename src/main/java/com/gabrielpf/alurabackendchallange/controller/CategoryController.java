package com.gabrielpf.alurabackendchallange.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/{id}")
    public ResponseEntity getOne(@PathVariable("id") UUID id) {
        Optional<CategoryDto> optionalCategoryDto = service.findById(id);

        if (optionalCategoryDto.isPresent())
            return ResponseEntity.ok(optionalCategoryDto.get());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
    }
}
