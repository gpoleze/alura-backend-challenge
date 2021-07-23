package com.gabrielpf.alurabackendchallange.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.gabrielpf.alurabackendchallange.exception.DataAlreadyExistsException;
import com.gabrielpf.alurabackendchallange.repository.VideoRepository;
import com.gabrielpf.alurabackendchallange.vo.in.VideosVoIn;
import com.gabrielpf.alurabackendchallange.vo.out.VideosVoOut;

@Service
public class VideoService {

    private final VideoRepository videoRepo;

    public VideoService(VideoRepository videoRepo) {this.videoRepo = videoRepo;}

    public VideosVoOut save(VideosVoIn videosVoIn) {
        if (videoRepo.findByTitle(videosVoIn.getTitle()).isPresent())
            throw new DataAlreadyExistsException(VideosVoIn.class, "title");

        var video = videosVoIn.convert();
        var savedVideo = videoRepo.save(video);
        return new VideosVoOut(savedVideo);
    }

    public List<VideosVoOut> findAll() {
        return videoRepo
                .findAll()
                .stream()
                .map(VideosVoOut::new)
                .toList();
    }

    public Optional<VideosVoOut> findById(UUID id) {
        return videoRepo
                .findById(id)
                .map(VideosVoOut::new);
    }

    public void delete(UUID id) {
        videoRepo
                .findById(id)
                .ifPresent(videoRepo::delete);
    }
}
