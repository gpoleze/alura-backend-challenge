package com.gabrielpf.alurabackendchallange.controller.form;


import javax.validation.constraints.Size;

import com.gabrielpf.alurabackendchallange.model.Video;

public class VideoUpdateForm implements UpdateForm<Video>{

    protected String description;

    @Size(max = 256)
    protected String title;

    protected String url;

    public VideoUpdateForm() {}

    public VideoUpdateForm(String title, String description, String url) {
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

    @Override
    public Video update(Video oldVideo) {
        Video newVideo = oldVideo.getCopy();
        if (title != null && !title.isBlank())
            newVideo.setTitle(title);
        if (description != null && !description.isBlank())
            newVideo.setDescription(description);
        if (url != null && !url.isBlank())
            newVideo.setUrl(url);

        return newVideo;
    }
}
