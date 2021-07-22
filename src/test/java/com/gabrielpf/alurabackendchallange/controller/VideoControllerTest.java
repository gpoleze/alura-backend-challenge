package com.gabrielpf.alurabackendchallange.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.gabrielpf.alurabackendchallange.model.Video;
import com.gabrielpf.alurabackendchallange.service.VideoService;
import com.gabrielpf.alurabackendchallange.vo.in.VideosVoIn;
import com.gabrielpf.alurabackendchallange.vo.out.VideosVoOut;
import com.google.gson.Gson;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VideoController.class)
class VideoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private VideoService videoService;

    @Test
    void listAllVideos() throws Exception {
        var videosVoOut = Arrays.asList(
                new Video(new VideosVoIn("desc1", "title1", "url1")),
                new Video(new VideosVoIn("desc2", "title2", "url2"))
        ).stream()
                .map(VideosVoOut::new)
                .toList();

        when(videoService.findAll()).thenReturn(videosVoOut);

        mockMvc
                .perform(get("/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(print());
    }

    @Test
    void succeedToGetOneVideosWhenIdExists() throws Exception {
        final var video1 = new Video(new VideosVoIn("desc1", "title1", "url1"));
        final var videoVoOut = new VideosVoOut(video1);

        when(videoService.findById(video1.getId()))
                .thenReturn(Optional.of(videoVoOut));

        mockMvc
                .perform(get("/videos/" + video1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(new Gson().toJson(videoVoOut)))
                .andDo(print());
    }

    @Test
    void failsToGetOneVideosWhenIdDoesNotExists() throws Exception {
        final var video1 = new Video(new VideosVoIn("desc1", "title1", "url1"));
        final var videoVoOut = new VideosVoOut(video1);

        when(videoService.findById(video1.getId()))
                .thenReturn(Optional.of(videoVoOut));
        var badId = UUID.randomUUID();

        mockMvc
                .perform(get("/videos/" + badId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not Found"));
    }

    @Test
    void failsToGetOneVideosWhenIdIsNotValid() throws Exception {
        final var video1 = new Video(new VideosVoIn("desc1", "title1", "url1"));
        final var videoVoOut = new VideosVoOut(video1);

        when(videoService.findById(video1.getId()))
                .thenReturn(Optional.of(videoVoOut));

        mockMvc
                .perform(get("/videos/not-valid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void videoIsSavedWhenFormIsValid() throws Exception {
        String title = "title1";
        String description = "desc1";
        String url = "url1";
        var gson = new Gson();

        VideosVoIn videosVoIn = new VideosVoIn(description, title, url);
        final var video = new Video(videosVoIn);
        final var videoVoOut = new VideosVoOut(video);

        when(videoService.save(any())).thenReturn(videoVoOut);

        Map<String, String> content = new HashMap<>();
        content.put("title", title);
        content.put("description", description);
        content.put("url", url);

        MockHttpServletRequestBuilder request = post("/videos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(content).getBytes());

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(gson.toJson(videoVoOut)))
                .andExpect(redirectedUrl("http://localhost/videos/" + videoVoOut.getId()));
    }


    private static Stream<Arguments> providereturnBadRequestWhenMissingArgument() {
        return Stream.of(
                Arguments.of("title", "description", ""),
                Arguments.of("title", null, "url"),
                Arguments.of(null, "description", "url"),
                Arguments.of("title", null, null),
                Arguments.of(null, "description", null),
                Arguments.of(null, null, "url"),
                Arguments.of(null, null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("providereturnBadRequestWhenMissingArgument")
    void returnBadRequestWhenMissingArgument(String title, String description, String url) throws Exception {

        Map<String, String> content = new HashMap<>();
        content.put("title", title);
        content.put("description", description);
        content.put("url", null);

        MockHttpServletRequestBuilder request = post("/videos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(content));

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(videoService, times(0)).save(any());
    }
}