package com.gabrielpf.alurabackendchallange.dto;

import java.util.UUID;

import javax.validation.constraints.NotBlank;

public record VideoCategoryDto(@NotBlank UUID id, @NotBlank VideoDto videoDto, @NotBlank CategoryDto categoryDto) {}
