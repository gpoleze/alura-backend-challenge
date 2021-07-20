package com.gabrielpf.alurabackendchallange.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/{id}")
    public ResponseEntity<VideosVoOut> getOne(@PathVariable("id") UUID id) {
        return videosRepo.findById(id)
                .map(VideosVoOut::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
