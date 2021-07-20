package com.gabrielpf.alurabackendchallange.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabrielpf.alurabackendchallange.repository.VideoRepository;

@RestController
@RequestMapping(value = "videos", produces = "application/json")
public class Video {


    private final VideoRepository videosRepo;

    public Video(VideoRepository videosRepo) {this.videosRepo = videosRepo;}

    @GetMapping
    public List<VideosVoOut> list() {
        return videosRepo.findAll().stream().map(VideosVoOut::new).toList();
    }

}
