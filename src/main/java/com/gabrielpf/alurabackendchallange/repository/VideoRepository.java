package com.gabrielpf.alurabackendchallange.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gabrielpf.alurabackendchallange.model.Video;
import com.gabrielpf.alurabackendchallange.service.specification.VideoSpecification;

@Repository
public interface VideoRepository extends CrudRepository<Video, UUID>, JpaSpecificationExecutor<Video> {
    Optional<Video> findByTitle(String title);

    List<Video> findAll(VideoSpecification videoSpecification);
}
