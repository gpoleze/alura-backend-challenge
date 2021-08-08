package com.gabrielpf.alurabackendchallange.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.gabrielpf.alurabackendchallange.controller.form.CategoryCreateForm;
import com.gabrielpf.alurabackendchallange.controller.form.CategoryUpdateForm;
import com.gabrielpf.alurabackendchallange.dto.CategoryDto;
import com.gabrielpf.alurabackendchallange.dto.VideoDto;
import com.gabrielpf.alurabackendchallange.exception.EntityDoesNotExistException;
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
    public ResponseEntity<CategoryDto> getOne(@PathVariable("id") UUID id) {
        Optional<CategoryDto> optionalCategoryDto = service.findById(id);


        return optionalCategoryDto
                .map(ResponseEntity::ok)
                .orElseThrow(EntityDoesNotExistException::new);
    }

    @PostMapping
    public ResponseEntity<CategoryDto> save(@Valid @RequestBody CategoryCreateForm form, UriComponentsBuilder uriBuilder) {
        var categoryDto = service.save(form);

        final URI uri = uriBuilder.path("/categories/{id}").buildAndExpand(categoryDto.id()).toUri();
        return ResponseEntity.created(uri).body(categoryDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryDto> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable UUID id, @Valid @RequestBody CategoryUpdateForm form) {
        if (form.hasAllFieldsBlank())
            return getOne(id);

        return service.update(id, form)
                .map(ResponseEntity::ok)
                .orElseThrow(EntityDoesNotExistException::new);
    }

    @GetMapping("/{id}/videos")
    public ResponseEntity<List<VideoDto>> listAllVideos(@PathVariable UUID id) {
        return service.findById(id)
                .map(categoryDto -> ResponseEntity.ok(service.listVideosByCategoryId(id)))
                .orElseThrow(EntityDoesNotExistException::new);
    }
}
