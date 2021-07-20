package com.gabrielpf.alurabackendchallange.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.gabrielpf.alurabackendchallange.vo.in.VideosVoIn;

@Entity
public class Video extends EntityWithUuidId {

    @Column(unique = true, nullable = false, length = 256)
    private String title;

    @Column
    private String description;

    @Column(nullable = false)
    private String url;

    protected Video() {}

    public Video(VideosVoIn in) {
        this.description = in.getDescription();
        this.url = in.getUrl();
        this.title = in.getTitle();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
