package com.gabrielpf.alurabackendchallange.dto;

import java.util.Objects;
import java.util.UUID;

import com.gabrielpf.alurabackendchallange.model.Video;

public class VideoDto {
    private final String description;
    private final UUID id;
    private final String title;
    private final String url;

    public VideoDto(Video video) {
        this.id = video.getId();
        this.description = video.getDescription();
        this.title = video.getTitle();
        this.url = video.getUrl();
    }

    public String getDescription() {
        return description;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoDto videoDto = (VideoDto) o;
        return Objects.equals(id, videoDto.id)
                && Objects.equals(title, videoDto.title)
                && Objects.equals(description, videoDto.description)
                && Objects.equals(url, videoDto.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, id, title, url);
    }
}
