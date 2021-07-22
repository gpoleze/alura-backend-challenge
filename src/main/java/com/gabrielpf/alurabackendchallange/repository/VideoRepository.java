package com.gabrielpf.alurabackendchallange.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gabrielpf.alurabackendchallange.model.Video;

@Repository
public interface VideoRepository extends CrudRepository<Video, UUID> {
    List<Video> findAll();
    Optional<Video> findByTitle(String title);
}
