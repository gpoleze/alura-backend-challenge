package com.gabrielpf.alurabackendchallange.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.gabrielpf.alurabackendchallange.controller.form.CategoryCreateForm;
import com.gabrielpf.alurabackendchallange.controller.form.CategoryUpdateForm;
import com.gabrielpf.alurabackendchallange.controller.form.VideoCreateForm;
import com.gabrielpf.alurabackendchallange.dto.CategoryDto;
import com.gabrielpf.alurabackendchallange.dto.VideoDto;
import com.gabrielpf.alurabackendchallange.exception.DataAlreadyExistsException;
import com.gabrielpf.alurabackendchallange.exception.EntityCannotBeDeletedException;
import com.gabrielpf.alurabackendchallange.model.Category;
import com.gabrielpf.alurabackendchallange.model.VideoCategory;
import com.gabrielpf.alurabackendchallange.repository.CategoryRepository;
import com.gabrielpf.alurabackendchallange.repository.VideoCategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository repository;
    private final VideoCategoryRepository videoCategoryRepository;

    public CategoryService(CategoryRepository repository, VideoCategoryRepository videoCategoryRepository) {
        this.repository = repository;
        this.videoCategoryRepository = videoCategoryRepository;
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
        if (!videoCategoryRepository.findByCategoryId(id).isEmpty())
            throw new EntityCannotBeDeletedException(VideoCategory.class, "id", "There are videos still attached to this category");

        repository.deleteById(id);
    }

    public Optional<CategoryDto> update(UUID id, CategoryUpdateForm form) {
        var category = repository.findById(id);

        if (category.isEmpty())
            return Optional.empty();

        Category updatedCategory = form.update(category.get());
        Category saved = repository.save(updatedCategory);

        return Optional.of(new CategoryDto(saved));
    }

    public List<VideoDto> listVideosByCategoryId(UUID id) {
        return videoCategoryRepository.findByCategoryId(id)
                .stream()
                .map(VideoCategory::getVideo)
                .map(VideoDto::new)
                .toList();
    }
}
