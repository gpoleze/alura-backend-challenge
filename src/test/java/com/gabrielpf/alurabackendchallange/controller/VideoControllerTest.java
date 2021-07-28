package com.gabrielpf.alurabackendchallange.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumingThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
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

import com.gabrielpf.alurabackendchallange.controller.form.VideoCreateForm;
import com.gabrielpf.alurabackendchallange.controller.form.VideoUpdateForm;
import com.gabrielpf.alurabackendchallange.dto.VideoDto;
import com.gabrielpf.alurabackendchallange.exception.DataAlreadyExistsException;
import com.gabrielpf.alurabackendchallange.model.Video;
import com.gabrielpf.alurabackendchallange.service.VideoService;
import com.google.gson.Gson;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VideoController.class)
class VideoControllerTest {

    private final String baseUrl = "/videos/";
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private VideoService videoService;
    private final Gson gson = new Gson();

    private byte[] getBody(String title, String description, String url) {
        Map<String, String> content = new HashMap<>();
        content.put("title", title);
        content.put("description", description);
        content.put("url", url);
        return gson.toJson(content).getBytes();
    }

    private VideoCreateForm getVideoVoIn() {
        return new VideoCreateForm("my description", "my tile", "example.com/video");
    }

    private VideoDto getVideoVoOut() {
        return new VideoDto(getVideoVoIn().convert());
    }

    @Test
    void listAllVideos() throws Exception {
        var videosVoOut = Stream.of(
                new Video(new VideoCreateForm("desc1", "title1", "url1")),
                new Video(new VideoCreateForm("desc2", "title2", "url2"))
        )
                .map(VideoDto::new)
                .toList();

        when(videoService.findAll()).thenReturn(videosVoOut);

        mockMvc
                .perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(print());
    }

    @Test
    void succeedToGetOneVideosWhenIdExists() throws Exception {
        final var video1 = new Video(new VideoCreateForm("desc1", "title1", "url1"));
        final var videoVoOut = new VideoDto(video1);

        when(videoService.findById(video1.getId()))
                .thenReturn(Optional.of(videoVoOut));

        mockMvc
                .perform(get(baseUrl + video1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(videoVoOut)))
                .andDo(print());
    }

    @Test
    void failsToGetOneVideosWhenIdDoesNotExists() throws Exception {
        final var video1 = new Video(new VideoCreateForm("desc1", "title1", "url1"));
        final var videoVoOut = new VideoDto(video1);

        when(videoService.findById(video1.getId()))
                .thenReturn(Optional.of(videoVoOut));
        var badId = UUID.randomUUID();

        mockMvc
                .perform(get(baseUrl + badId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not Found"));
    }

    @Test
    void failsToGetOneVideosWhenIdIsNotValid() throws Exception {
        final var video1 = new Video(new VideoCreateForm("desc1", "title1", "url1"));
        final var videoVoOut = new VideoDto(video1);

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

        VideoCreateForm videoCreateForm = new VideoCreateForm(description, title, url);
        final var video = new Video(videoCreateForm);
        final var videoVoOut = new VideoDto(video);

        when(videoService.save(any())).thenReturn(videoVoOut);

        MockHttpServletRequestBuilder request = post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getBody(title, description, url));

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(gson.toJson(videoVoOut)))
                .andExpect(redirectedUrl("http://localhost/videos/" + videoVoOut.getId()));
    }

    private static Stream<Arguments> providerReturnBadRequestWhenMissingArgument() {
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
    @MethodSource("providerReturnBadRequestWhenMissingArgument")
    void returnBadRequestWhenMissingArgument(String title, String description, String url) throws Exception {

        Map<String, String> content = new HashMap<>();
        content.put("title", title);
        content.put("description", description);
        content.put("url", url);

        MockHttpServletRequestBuilder request = post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(content));

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(videoService, times(0)).save(any());
    }

    @Test
    void returnBadRequestWhenBodyArgumentsAreValidButTitleAlreadyExists() throws Exception {
        String title = "title";
        String description = "desc";
        String url = "url";

        when(videoService.save(any(VideoCreateForm.class))).thenThrow(new DataAlreadyExistsException(VideoCreateForm.class, "title"));

        MockHttpServletRequestBuilder request = post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getBody(title, description, url));


        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"field\":\"title\", \"error\":\"The given value is already present in the database\"}"))
                .andDo(print());
    }

    @Test
    void deleteVideoWhenItemExistsInTheDatabase() throws Exception {
        VideoDto videoVoOut = getVideoVoOut();

        doNothing().when(videoService).delete(videoVoOut.getId());

        mockMvc.perform(delete(baseUrl + videoVoOut.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void returnASuccessStatusWhenTryingToDeleteAnItemThatDoesNotExistInTheDatabase() throws Exception {
        doNothing().when(videoService).delete(any());

        mockMvc.perform(delete(baseUrl + UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    void sendBadRequestResponseWhenIdIsInvalidAndTryingToDeleteAVideo() throws Exception {
        mockMvc.perform(delete("/videos/i-am-clearly-not-a-valid-uuid"))
                .andExpect(status().isBadRequest());
    }


    private static Stream<Arguments> providerReturnUpdatedVideoWhenRequestIsCorrect() {
        Function<String[][], Map<String, String>> getMap = stringArray -> Stream.of(stringArray).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        return Stream.of(
                Arguments.of(getMap.apply(new String[][]{{"title", "newTitle"}})),
                Arguments.of(getMap.apply(new String[][]{{"description", "newdescription"}})),
                Arguments.of(getMap.apply(new String[][]{{"url", "newUrl"}})),
                Arguments.of(getMap.apply(new String[][]{{"title", "newTitle"}, {"description", "newdescription"}})),
                Arguments.of(getMap.apply(new String[][]{{"title", "newTitle"}, {"url", "newUrl"}})),
                Arguments.of(getMap.apply(new String[][]{{"url", "newUrl"}, {"description", "newdescription"}})),
                Arguments.of(getMap.apply(new String[][]{{"title", "newTitle"}, {"description", "newdescription"}, {"url", "newUrl"}}))
        );
    }

    @ParameterizedTest
    @MethodSource("providerReturnUpdatedVideoWhenRequestIsCorrect")
    void returnUpdatedVideoWhenRequestIsCorrect(Map<String, String> items) throws Exception {
        String body = gson.toJson(items);
        VideoUpdateForm payload = gson.fromJson(body, VideoUpdateForm.class);
        Video converted = getVideoVoIn().convert();
        Video updated = payload.update(converted);
        VideoDto videoVoOut = new VideoDto(updated);

        doReturn(Optional.of(videoVoOut))
                .when(videoService)
                .update(eq(videoVoOut.getId()), any(VideoUpdateForm.class));

        var response = mockMvc.perform(
                patch(baseUrl + videoVoOut.getId()).contentType(MediaType.APPLICATION_JSON).content(body)
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        VideoDto responseVideo = gson.fromJson(response, VideoDto.class);

        assertNotNull(responseVideo.getTitle());
        assertNotNull(responseVideo.getDescription());
        assertNotNull(responseVideo.getUrl());

        assumingThat(payload.getTitle() != null, () -> assertEquals(payload.getTitle(), responseVideo.getTitle()));
        assumingThat(payload.getDescription() != null, () -> assertEquals(payload.getDescription(), responseVideo.getDescription()));
        assumingThat(payload.getUrl() != null, () -> assertEquals(payload.getUrl(), responseVideo.getUrl()));

    }

    @Test
    void returnVideoWithoutAnyChangesWhenTryingToUpdateVideoAndTheGivenIdExistsButBodyIsEmpty() throws Exception {
        VideoDto videoVoOut = getVideoVoOut();
        doReturn(Optional.of(videoVoOut)).when(videoService).findById(videoVoOut.getId());

        mockMvc.perform(
                patch(baseUrl + videoVoOut.getId()).contentType(MediaType.APPLICATION_JSON).content("{}")
        )
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(videoVoOut)));

    }

    @Test
    void returnBadRequestWhenUpdatingVideoAndIdISNotValid() throws Exception {
        mockMvc.perform(
                patch(baseUrl + "definitly-not-valid-uuid").contentType(MediaType.APPLICATION_JSON).content("{}")
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnBadRequestUpdatingVideoAndTheGivenIdDoesntExist() throws Exception {
        doReturn(Optional.empty()).when(videoService).update(UUID.randomUUID(), new VideoUpdateForm());
        mockMvc.perform(patch(baseUrl + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isNotFound());
    }


}