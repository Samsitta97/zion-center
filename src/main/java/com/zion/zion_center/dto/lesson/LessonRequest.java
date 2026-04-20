package com.zion.zion_center.dto.lesson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LessonRequest(
        @NotNull Long classId,
        @NotBlank String title,
        String description,
        @NotBlank String youtubeUrl,
        Integer durationSeconds,
        @NotNull Long categoryId
) {}
