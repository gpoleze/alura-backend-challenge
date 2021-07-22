package com.gabrielpf.alurabackendchallange.vo.in;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.gabrielpf.alurabackendchallange.model.Video;

public class VideosVoIn {

    @NotBlank
    @NotNull
    private String description;

    @NotBlank
    @NotNull
    @Size(max = 256)
    private String title;

    @NotBlank
    @NotNull
    private String url;

    protected VideosVoIn(){}

    public VideosVoIn(String description, String title, String url) {
        this.description = description;
        this.title = title;
        this.url = url;
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
