package com.gabrielpf.alurabackendchallange.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gabrielpf.alurabackendchallange.model.VideoCategory;

@Repository
public interface VideoCategoryRepository extends CrudRepository<VideoCategory, UUID> {
    Optional<VideoCategory> findByCategoryId(UUID id);
}
