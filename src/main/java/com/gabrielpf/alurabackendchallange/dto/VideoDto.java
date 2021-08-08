package com.gabrielpf.alurabackendchallange.dto;

import java.util.UUID;

import com.gabrielpf.alurabackendchallange.model.Video;

public record VideoDto(UUID id, String title, String description, String url) {
    public VideoDto(Video video) {
        this(video.getId(), video.getTitle(), video.getDescription(),video.getUrl());
    }
}
