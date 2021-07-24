package com.gabrielpf.alurabackendchallange.controller.form;


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.validation.constraints.Size;

import com.gabrielpf.alurabackendchallange.model.Video;

public class VideoUpdateForm {

    private String description;

    @Size(max = 256)
    private String title;

    private String url;

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

    public boolean hasAllFieldsBlank() {
        Predicate<Field> hasValue = f -> {
            try {
                return f.get(this) != null && !((String) f.get(this)).isBlank();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return true;
            }
        };

        return Arrays.stream(this.getClass().getDeclaredFields()).noneMatch(hasValue);
    }
}
