package com.gabrielpf.alurabackendchallange.service;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.gabrielpf.alurabackendchallange.model.Video;
import com.gabrielpf.alurabackendchallange.repository.VideoRepository;
import com.gabrielpf.alurabackendchallange.vo.in.VideosVoIn;
import com.gabrielpf.alurabackendchallange.vo.out.VideosVoOut;

@SpringBootTest(classes = {VideoService.class})
class VideoServiceTest {

    @Autowired
    private VideoService service;

    @MockBean
    private VideoRepository repository;

    private VideosVoIn getVideoVoIn() {
        return new VideosVoIn("my description", "my tile", "example.com/video");
    }

    private VideosVoOut getVideoVoOut() {
        return new VideosVoOut(getVideoVoIn().convert());
    }

    @Test
    void deleteMethodReturnsVoidWhenThereIsAMatchingId() {
        Video video = getVideoVoIn().convert();

        doReturn(Optional.of(video))
                .when(repository)
                .findById(video.getId());

        service.delete(video.getId());
        verify(repository, times(1)).findById(video.getId());
        verify(repository, times(1)).delete(video);

    }

    @Test
    void deleteMethodReturnsVoidWhenThereIsNotAMatchingId() {
        Video video = getVideoVoIn().convert();

        doReturn(Optional.empty())
                .when(repository)
                .findById(video.getId());

        service.delete(video.getId());
        verify(repository, times(1)).findById(video.getId());
        verify(repository, times(0)).delete(video);
    }
}