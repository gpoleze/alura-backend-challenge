package com.gabrielpf.alurabackendchallange.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.gabrielpf.alurabackendchallange.controller.form.CategoryCreateForm;
import com.gabrielpf.alurabackendchallange.controller.form.VideoCreateForm;
import com.gabrielpf.alurabackendchallange.dto.CategoryDto;
import com.gabrielpf.alurabackendchallange.exception.DataAlreadyExistsException;
import com.gabrielpf.alurabackendchallange.model.Category;
import com.gabrielpf.alurabackendchallange.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<CategoryDto> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), true)
                .map(CategoryDto::new)
                .toList();
    }

    public Optional<CategoryDto> findById(UUID id) {
        return repository.findById(id)
                .map(CategoryDto::new);
    }

    public CategoryDto save(CategoryCreateForm form) {
        if (repository.findByTitle(form.title()).isPresent())
            throw new DataAlreadyExistsException(VideoCreateForm.class, "title");

        Category savedCategory = repository.save(form.convert());
        return new CategoryDto(savedCategory);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
