package com.gabrielpf.alurabackendchallange.service.specification;

import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.gabrielpf.alurabackendchallange.model.Video;

public class VideoSpecification {

    public static Specification<Video> likeTitle(Optional<String> title) {
        if (title.isEmpty()) return null;

        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }
}
