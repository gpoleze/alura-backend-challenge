package com.gabrielpf.alurabackendchallange.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gabrielpf.alurabackendchallange.model.Video;
import com.gabrielpf.alurabackendchallange.service.VideoService;
import com.gabrielpf.alurabackendchallange.vo.in.VideosVoIn;
import com.gabrielpf.alurabackendchallange.vo.out.VideosVoOut;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class VideoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private VideoService videoService;

    @Test
    void listAllVideos() throws Exception {
        var videosVoOut = Arrays.asList(
                new Video(new VideosVoIn("desc1","title1", "url1")),
                new Video(new VideosVoIn("desc2","title2", "url2"))
                ).stream()
                .map(VideosVoOut::new)
                .toList();

        when(videoService.findAll()).thenReturn(videosVoOut);

        mockMvc
                .perform(MockMvcRequestBuilders.get("/videos").contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andDo(print());
    }
}