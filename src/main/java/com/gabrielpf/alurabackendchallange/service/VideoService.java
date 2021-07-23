package com.gabrielpf.alurabackendchallange.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.gabrielpf.alurabackendchallange.controller.form.VideoUpdateForm;
import com.gabrielpf.alurabackendchallange.exception.DataAlreadyExistsException;
import com.gabrielpf.alurabackendchallange.model.Video;
import com.gabrielpf.alurabackendchallange.repository.VideoRepository;
import com.gabrielpf.alurabackendchallange.controller.form.VideoCreateForm;
import com.gabrielpf.alurabackendchallange.dto.VideoDto;

@Service
public class VideoService {

    private final VideoRepository videoRepo;

    public VideoService(VideoRepository videoRepo) {this.videoRepo = videoRepo;}

    public VideoDto save(VideoCreateForm videoCreateForm) {
        if (videoRepo.findByTitle(videoCreateForm.getTitle()).isPresent())
            throw new DataAlreadyExistsException(VideoCreateForm.class, "title");

        var video = videoCreateForm.convert();
        var savedVideo = videoRepo.save(video);
        return new VideoDto(savedVideo);
    }

    public List<VideoDto> findAll() {
        return videoRepo
                .findAll()
                .stream()
                .map(VideoDto::new)
                .toList();
    }

    public Optional<VideoDto> findById(UUID id) {
        return videoRepo
                .findById(id)
                .map(VideoDto::new);
    }

    public void delete(UUID id) {
        videoRepo
                .findById(id)
                .ifPresent(videoRepo::delete);
    }

    public Optional<VideoDto> update(UUID id, VideoUpdateForm videoUpdateForm) {

        Optional<Video> videoOptional = videoRepo.findById(id);

        if(videoOptional.isPresent()) {
            Video updatedVideo = videoUpdateForm.update(videoOptional.get());
            videoRepo.save(updatedVideo);
            return Optional.of(new VideoDto(updatedVideo));
        }

        return Optional.empty();
    }
}
