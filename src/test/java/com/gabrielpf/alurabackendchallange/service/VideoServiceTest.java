package com.gabrielpf.alurabackendchallange.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTestContextBootstrapper;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.gabrielpf.alurabackendchallange.controller.form.VideoCreateForm;
import com.gabrielpf.alurabackendchallange.controller.form.VideoUpdateForm;
import com.gabrielpf.alurabackendchallange.dto.VideoDto;
import com.gabrielpf.alurabackendchallange.exception.DataAlreadyExistsException;
import com.gabrielpf.alurabackendchallange.model.Video;
import com.gabrielpf.alurabackendchallange.repository.VideoRepository;
import com.gabrielpf.alurabackendchallange.service.specification.VideoSpecification;

@WebMvcTest(VideoService.class)
class VideoServiceTest {

    @Autowired
    private VideoService service;

    @MockBean
    private VideoRepository repository;

    private Video getVideo() {
        return new Video("my title", "description", "url");
    }

    private VideoCreateForm getVideoCreateForm() {
        return getVideoCreateForm("my title");
    }

    private VideoCreateForm getVideoCreateForm(String title) {
        return new VideoCreateForm(title, "my tile", "example.com/video");
    }

    private VideoDto getVideoDto() {
        return new VideoDto(getVideoCreateForm().convert());
    }

    private VideoDto getVideoDto(String title) {
        return new VideoDto(getVideoCreateForm(title).convert());
    }

    private static final List<Video> videosList =
            List.of(
                    new Video("my first video", "was very nice", "youtube.com/my-fisrt-video"),
                    new Video("Reaction", "Trying to cook past", "youtube.com/reaction"),
                    new Video("John Doe videocast", "This week interviewing someone you do not know", "vimeo.com/john-doe-videocast"),
                    new Video("See what happens", "The prank that got wrong", "youtube.com/see-what-happens"),
                    new Video("The video of all videos", "The video to rule them all", "youtube.com/the-video-of-all-videos")
            );

    @Test
    void deleteMethodReturnsVoidWhenThereIsAMatchingId() {
        Video video = getVideoCreateForm().convert();

        doReturn(Optional.of(video))
                .when(repository)
                .findById(video.getId());

        service.delete(video.getId());
        verify(repository, times(1)).findById(video.getId());
        verify(repository, times(1)).delete(video);

    }

    @Test
    void deleteMethodReturnsVoidWhenThereIsNotAMatchingId() {
        Video video = getVideoCreateForm().convert();

        doReturn(Optional.empty())
                .when(repository)
                .findById(video.getId());

        service.delete(video.getId());
        verify(repository, times(1)).findById(video.getId());
        verify(repository, times(0)).delete(video);
    }

    @Test
    void findAllMethodReturnsAListOfVideoDtos() {
        List<VideoDto> expected = videosList.stream().map(VideoDto::new).toList();

        doReturn(videosList).when(repository).findAll(VideoSpecification.likeTitle(Optional.empty()));

        List<VideoDto> actual = service.findAll(VideoSpecification.likeTitle(Optional.empty()));

        verify(repository, times(1)).findAll(VideoSpecification.likeTitle(Optional.empty()));

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> findAllMethodReturnsAListOfVideoDtosMatchingCriteriaProvider() {
        Function<String, List<Video>> filterVideoDtoList = search -> videosList
                .stream()
                .filter(video -> video.getTitle().toLowerCase().contains(search.toLowerCase()))
                .toList();

        return Stream.of(
                Arguments.of(Optional.of("video"), filterVideoDtoList.apply("video")),
                Arguments.of(Optional.of("nas"), filterVideoDtoList.apply("nas")),
                Arguments.of(Optional.of("reaction"), filterVideoDtoList.apply("reaction")),
                Arguments.of(Optional.of("John Doe"), filterVideoDtoList.apply("John Doe")),
                Arguments.of(Optional.empty(), videosList)

        );
    }

    @ParameterizedTest
    @MethodSource("findAllMethodReturnsAListOfVideoDtosMatchingCriteriaProvider")
    void findAllMethodReturnsAListOfVideoDtosMatchingCriteria(Optional<String> searchItem, List<Video> videos) {
        List<VideoDto> expected = videos.stream().map(VideoDto::new).toList();

        doReturn(videos).when(repository).findAll(VideoSpecification.likeTitle(searchItem));

        List<VideoDto> actual = service.findAll(VideoSpecification.likeTitle(searchItem));

        verify(repository, times(1)).findAll(any(VideoSpecification.class));

        assertEquals(expected, actual);
    }

    @Test
    void findAllMethodReturnsAnEmptyListIfNoVideosExist() {
        doReturn(Collections.emptyList()).when(repository).findAll();

        List<VideoDto> actual = service.findAll(VideoSpecification.likeTitle(Optional.empty()));

        verify(repository, times(1)).findAll(VideoSpecification.likeTitle(Optional.empty()));

        assertTrue(actual.isEmpty());
    }

    @Test
    void findByIdReturnsOneItemIfItExists() {
        final Video video = getVideo();
        doReturn(Optional.of(video)).when(repository).findById(video.getId());

        Optional<VideoDto> actual = service.findById(video.getId());

        verify(repository, times(1)).findById(video.getId());

        assertTrue(actual.isPresent());
        assertEquals(new VideoDto(video), actual.get());

    }

    @Test
    void findByIdReturnsEmptyOpytionalIfItemIfDoesNotExists() {
        var id = UUID.randomUUID();
        doReturn(Optional.empty()).when(repository).findById(id);

        Optional<VideoDto> actual = service.findById(id);

        verify(repository, times(1)).findById(id);

        assertFalse(actual.isPresent());
    }


    @Test
    void saveReturnVideoDtOIfObjectNotInTheDatabase() {
        VideoCreateForm videoCreateForm = getVideoCreateForm();
        Video video = videoCreateForm.convert();

        doReturn(Optional.empty()).when(repository).findByTitle(video.getTitle());
        doReturn(Optional.empty()).when(repository).findById(video.getId());
        doReturn(video).when(repository).save(any(Video.class));

        VideoDto actual = service.save(videoCreateForm);

        verify(repository, times(1)).findByTitle(video.getTitle());
        verify(repository, times(1)).save(any(Video.class));
        assertEquals(new VideoDto(video), actual);
    }

    @Test
    void saveThrowsExceptionWhenItemWithSameTitleAlreadyInTheDatabase() {
        VideoCreateForm videoCreateForm = getVideoCreateForm();
        Video video = videoCreateForm.convert();

        doReturn(Optional.of(video)).when(repository).findByTitle(videoCreateForm.getTitle());
        doReturn(video).when(repository).save(any(Video.class));

        assertThrows(DataAlreadyExistsException.class, () -> service.save(videoCreateForm));

        verify(repository, times(1)).findByTitle(video.getTitle());
        verify(repository, times(0)).save(any(Video.class));

    }

    @Test
    void returnEmptyOptionalIfIdisNotInTheDatabase() {
        var id = UUID.randomUUID();

        doReturn(Optional.empty()).when(repository).findById(id);

        Optional<VideoDto> actual = service.update(id, null);

        assertTrue(actual.isEmpty());
        verify(repository, times(1)).findById(id);
    }

    @ParameterizedTest
    @CsvSource({
            "'','',''",
            ",,",
            "new,,",
            ",new,",
            ",,new",
            "new,new,",
            "new,,new",
            ",new,new",
            "new,new,new",
    })
    void returnVideoWithoutAnyChangeDTOIfIsInTheDatabaseBut(String title, String description, String url) {
        Video video = getVideo();
        if (title != null && !title.isBlank())
            video.setTitle(title);
        if (description != null && !description.isBlank())
            video.setDescription(description);
        if (url != null && !url.isBlank())
            video.setUrl(url);

        VideoDto expected = new VideoDto(video);
        VideoUpdateForm videoUpdateForm = new VideoUpdateForm(title, description, url);

        doReturn(Optional.of(video)).when(repository).findById(video.getId());

        Optional<VideoDto> actual = service.update(video.getId(), videoUpdateForm);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        assertEquals(expected, actual.get());
        assertEquals(expected, actual.get());

        verify(repository, times(1)).findById(video.getId());
        verify(repository, times(1)).save(any(Video.class));

    }


}
