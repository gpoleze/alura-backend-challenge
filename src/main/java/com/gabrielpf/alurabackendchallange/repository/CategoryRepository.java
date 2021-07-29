package com.gabrielpf.alurabackendchallange.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gabrielpf.alurabackendchallange.model.Category;

@Repository
public interface CategoryRepository extends CrudRepository<Category, UUID> {
}
