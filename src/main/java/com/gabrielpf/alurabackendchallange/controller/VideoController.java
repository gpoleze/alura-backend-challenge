package com.gabrielpf.alurabackendchallange.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabrielpf.alurabackendchallange.service.VideoService;
import com.gabrielpf.alurabackendchallange.vo.in.VideosVoIn;
import com.gabrielpf.alurabackendchallange.vo.out.VideosVoOut;

@RestController
@RequestMapping(value = "videos", produces = MediaType.APPLICATION_JSON_VALUE)
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {this.videoService = videoService;}

    @GetMapping
    public List<VideosVoOut> list() {
        return videoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideosVoOut> getOne(@PathVariable("id") UUID id) {
        return videoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public VideosVoOut create(@Valid @RequestBody VideosVoIn videosVoIn) {
        return videoService.save(videosVoIn);
    }

}
