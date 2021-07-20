package com.gabrielpf.alurabackendchallange.vo.in;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.gabrielpf.alurabackendchallange.model.Video;

public class VideosVoIn {

    @NotBlank
    private final String description;

    @NotBlank
    @Size(max = 256)
    private final String title;

    @NotBlank
    private final String url;


    public VideosVoIn(String description, String title, String url) {
        this.description = description.strip();
        this.title = title.strip();
        this.url = url.strip();
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Video convert() {
        return new Video(this);
    }
}
