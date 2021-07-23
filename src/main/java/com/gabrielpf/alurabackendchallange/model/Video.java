package com.gabrielpf.alurabackendchallange.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.gabrielpf.alurabackendchallange.controller.form.VideoCreateForm;

@Entity
public class Video extends EntityWithUuidId {

    @Column(unique = true, nullable = false, length = 256)
    private String title;

    @Column
    private String description;

    @Column(nullable = false)
    private String url;

    protected Video() {}

    public Video(VideoCreateForm in) {
        this.description = in.getDescription();
        this.url = in.getUrl();
        this.title = in.getTitle();
    }

    private Video(Video oldVideo) {
        super(oldVideo.getId());
        this.title = oldVideo.title;
        this.description = oldVideo.description;
        this.url = oldVideo.url;
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

    public Video getCopy() {
        return new Video(this);
    }
}
