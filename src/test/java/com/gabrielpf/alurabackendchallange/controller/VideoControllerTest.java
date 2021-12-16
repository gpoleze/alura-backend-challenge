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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabrielpf.alurabackendchallange.controller.form.VideoCreateForm;
import com.gabrielpf.alurabackendchallange.controller.form.VideoUpdateForm;
import com.gabrielpf.alurabackendchallange.dto.VideoDto;
import com.gabrielpf.alurabackendchallange.exception.DataAlreadyExistsException;
import com.gabrielpf.alurabackendchallange.model.Video;
import com.gabrielpf.alurabackendchallange.service.VideoService;
import com.gabrielpf.alurabackendchallange.service.specification.VideoSpecification;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    private byte[] getBody(String title, String description, String url) {
        Map<String, String> content = new HashMap<>();
        content.put("title", title);
        content.put("description", description);
        content.put("url", url);
        return gson.toJson(content).getBytes();
    }

    private VideoCreateForm getVideoCreateForm() {
        return new VideoCreateForm("my description", "my tile", "example.com/video");
    }

    private VideoDto getVideoDto() {
        return new VideoDto(UUID.randomUUID(), "my tile", "my description", "example.com/video");
    }

    private VideoDto getVideoDto(String title, String description, String url) {
        return new VideoDto(UUID.randomUUID(), title, description, url);
    }

    @Nested
    @DisplayName("findAll method")
    class FindAllUnitTest {
        private static final List<VideoDto> videoDtoList =
                List.of(
                        new VideoDto(UUID.randomUUID(), "my first video", "was very nice", "youtube.com/my-fisrt-video"),
                        new VideoDto(UUID.randomUUID(), "Reaction", "Trying to cook past", "youtube.com/reaction"),
                        new VideoDto(UUID.randomUUID(), "John Doe videocast", "This week interviewing someone you do not know", "vimeo.com/john-doe-videocast"),
                        new VideoDto(UUID.randomUUID(), "See what happens", "The prank that got wrong", "youtube.com/see-what-happens"),
                        new VideoDto(UUID.randomUUID(), "The video of all videos", "The video to rule them all", "youtube.com/the-video-of-all-videos")
                );

        private static Stream<Arguments> returnVideosWhenSearchMatchesProvider() {
            Function<String, List<VideoDto>> filterVideoDtoList = search -> videoDtoList
                    .stream()
                    .filter(video -> video.title().toLowerCase().contains(search.toLowerCase()))
                    .toList();

            return Stream.of(
                    Arguments.of(Optional.of("video"), filterVideoDtoList.apply("video")),
                    Arguments.of(Optional.of("nas"), filterVideoDtoList.apply("nas")),
                    Arguments.of(Optional.of("reaction"), filterVideoDtoList.apply("reaction")),
                    Arguments.of(Optional.of("John Doe"), filterVideoDtoList.apply("John Doe")),
                    Arguments.of(Optional.empty(), videoDtoList)

            );
        }


        @ParameterizedTest
        @MethodSource("returnVideosWhenSearchMatchesProvider")
        void returnVideosWhenSearchMatches(Optional<String> searchItem, List<VideoDto> videosDto) throws Exception {
            doReturn(videosDto).when(videoService).findAll(VideoSpecification.likeTitle(searchItem));

            final var request = searchItem
                    .map(item -> get(baseUrl).queryParam("search", item))
                    .orElse(get(baseUrl));

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(videosDto.size())))
                    .andExpect(content().json(objectMapper.writeValueAsString(videosDto)))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("getOne one")
    class GetOneUnitTest {
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
                    .andExpect(content().string("{\"field\":\"id\",\"error\":\"Item Not Found\"}"));
        }

        @Test
        void failsToGetOneVideosWhenIdIsNotValid() throws Exception {
            final var video1 = new Video(new VideoCreateForm("desc1", "title1", "url1"));
            final var videoVoOut = new VideoDto(video1);

            when(videoService.findById(video1.getId()))
                    .thenReturn(Optional.of(videoVoOut));

            mockMvc
                    .perform(get(baseUrl + "not-valid-uuid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Create one")
    class CreateOneUnitTest {
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
                    .andExpect(redirectedUrl("http://localhost" + baseUrl + videoVoOut.id()));
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
    }

    @Nested
    @DisplayName("Delete video")
    class DeleteVideoUnitTest {
        @Test
        void deleteVideoWhenItemExistsInTheDatabase() throws Exception {
            VideoDto videoVoOut = getVideoDto();

            doNothing().when(videoService).delete(videoVoOut.id());

            mockMvc.perform(delete(baseUrl + videoVoOut.id()))
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
            mockMvc.perform(delete(baseUrl + "i-am-clearly-not-a-valid-uuid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Update video")
    class UpdateVideoUnitTest {
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
            Video converted = getVideoCreateForm().convert();
            Video updated = payload.update(converted);
            VideoDto videoVoOut = new VideoDto(updated);

            doReturn(Optional.of(videoVoOut))
                    .when(videoService)
                    .update(eq(videoVoOut.id()), any(VideoUpdateForm.class));

            var response = mockMvc.perform(
                            patch(baseUrl + videoVoOut.id()).contentType(MediaType.APPLICATION_JSON).content(body)
                    )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            VideoDto responseVideo = objectMapper.readValue(response, VideoDto.class);

            assertNotNull(responseVideo.title());
            assertNotNull(responseVideo.description());
            assertNotNull(responseVideo.url());

            assumingThat(payload.getTitle() != null, () -> assertEquals(payload.getTitle(), responseVideo.title()));
            assumingThat(payload.getDescription() != null, () -> assertEquals(payload.getDescription(), responseVideo.description()));
            assumingThat(payload.getUrl() != null, () -> assertEquals(payload.getUrl(), responseVideo.url()));

        }

        @Test
        void returnVideoWithoutAnyChangesWhenTryingToUpdateVideoAndTheGivenIdExistsButBodyIsEmpty() throws Exception {
            VideoDto videoVoOut = getVideoDto();
            doReturn(Optional.of(videoVoOut)).when(videoService).findById(videoVoOut.id());

            mockMvc.perform(
                            patch(baseUrl + videoVoOut.id()).contentType(MediaType.APPLICATION_JSON).content("{}")
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
}