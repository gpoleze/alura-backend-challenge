package com.gabrielpf.alurabackendchallange.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.gabrielpf.alurabackendchallange.controller.form.VideoCreateForm;
import com.gabrielpf.alurabackendchallange.controller.form.VideoUpdateForm;
import com.gabrielpf.alurabackendchallange.dto.VideoDto;
import com.gabrielpf.alurabackendchallange.exception.EntityDoesNotExistException;
import com.gabrielpf.alurabackendchallange.service.VideoService;
import com.gabrielpf.alurabackendchallange.service.specification.VideoSpecification;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "videos", produces = MediaType.APPLICATION_JSON_VALUE)
@EnableSpringDataWebSupport
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public List<VideoDto> list(@RequestParam Optional<String> search) {
        return videoService.findAll(VideoSpecification.likeTitle(search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoDto> getOne(@PathVariable("id") UUID id) {
        return videoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(EntityDoesNotExistException::new);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VideoDto> create(@Valid @RequestBody VideoCreateForm videoCreateForm, UriComponentsBuilder uriBuilder) {
        var videosVoOut = videoService.save(videoCreateForm);

        final URI uri = uriBuilder.path("/videos/{id}").buildAndExpand(videosVoOut.id()).toUri();
        return ResponseEntity.created(uri).body(videosVoOut);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<VideoDto> delete(@PathVariable UUID id) {
        videoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VideoDto> patchVideo(@PathVariable UUID id, @Valid @RequestBody VideoUpdateForm videoUpdateForm) {
        if (videoUpdateForm.hasAllFieldsBlank())
            return getOne(id);

        return videoService
                .update(id, videoUpdateForm)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

}
