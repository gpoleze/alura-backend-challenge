package com.gabrielpf.alurabackendchallange.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gabrielpf.alurabackendchallange.model.VideoCategory;

@Repository
public interface VideoCategoryRepository extends CrudRepository<VideoCategory, UUID> {
    List<VideoCategory> findByCategoryId(UUID id);
}
