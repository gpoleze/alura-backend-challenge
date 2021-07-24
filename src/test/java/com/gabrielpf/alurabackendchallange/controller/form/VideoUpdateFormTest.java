package com.gabrielpf.alurabackendchallange.controller.form;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.gabrielpf.alurabackendchallange.model.Video;

class VideoUpdateFormTest {

    @ParameterizedTest
    @CsvSource({
            ",,",
            "'',,",
            ",'',",
            ",,''",
            "'','',",
            "'',,''",
            ",'',''",
            "'','',''",
            "foo,,",
            ",foo,",
            ",,foo",
            "foo,foo,",
            "foo,,foo",
            ",foo,foo",
            "foo,foo,foo",
    })
    void update(String title, String description, String url) {
        VideoUpdateForm videoUpdateForm = new VideoUpdateForm(title, description, url);
        Video video = new Video("title", "description", "url");
        Video actual = videoUpdateForm.update(video);

        if (title != null && !title.isBlank())
            assertEquals(title, actual.getTitle());
        if (description != null && !description.isBlank())
            assertEquals(description, actual.getDescription());
        if (url != null && !url.isBlank())
            assertEquals(url, actual.getUrl());
    }

    @ParameterizedTest
    @CsvSource({
            ",,,true",
            "'',,,true",
            ",'',,true",
            ",,'',true",
            "'','',,true",
            "'',,'',true",
            ",'','',true",
            "'','','',true",
            "foo,,,false",
            ",foo,,false",
            ",,foo,false",
            "foo,foo,,false",
            "foo,,foo,false",
            ",foo,foo,false",
            "foo,foo,foo,false",
    })
    void hasAllFieldsNull(String title, String description, String url, boolean expected) {
        VideoUpdateForm videoUpdateForm = new VideoUpdateForm(title, description, url);
        boolean actual = videoUpdateForm.hasAllFieldsBlank();
        assertEquals(expected, actual);
    }
}