package com.gabrielpf.alurabackendchallange.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.gabrielpf.alurabackendchallange.dto.CategoryDto;
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
}
