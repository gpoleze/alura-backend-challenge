package com.gabrielpf.alurabackendchallange.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.gabrielpf.alurabackendchallange.controller.form.VideoCreateForm;
import com.gabrielpf.alurabackendchallange.controller.form.VideoUpdateForm;
import com.gabrielpf.alurabackendchallange.service.VideoService;
import com.gabrielpf.alurabackendchallange.dto.VideoDto;

@RestController
@RequestMapping(value = "videos", produces = MediaType.APPLICATION_JSON_VALUE)
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {this.videoService = videoService;}

    @GetMapping
    public List<VideoDto> list() {
        return videoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity getOne(@PathVariable("id") UUID id) {
        Optional<VideoDto> voOut = videoService.findById(id);

        if (voOut.isPresent())
            return ResponseEntity.ok(voOut);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VideoDto> create(@Valid @RequestBody VideoCreateForm videoCreateForm, UriComponentsBuilder uriBuilder) {
        var videosVoOut = videoService.save(videoCreateForm);

        final URI uri = uriBuilder.path("/videos/{id}").buildAndExpand(videosVoOut.getId()).toUri();
        return ResponseEntity.created(uri).body(videosVoOut);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable UUID id) {
        videoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity patchVideo(@PathVariable UUID id, @Valid @RequestBody VideoUpdateForm videoUpdateForm) {
        if (videoUpdateForm.hasAllFieldsNull())
            return getOne(id);


        Optional<VideoDto> optionalVideosVoOut = videoService.update(id, videoUpdateForm);
        if (optionalVideosVoOut.isPresent())
            return ResponseEntity.ok(optionalVideosVoOut.get());
        return ResponseEntity.noContent().build();
    }

}
