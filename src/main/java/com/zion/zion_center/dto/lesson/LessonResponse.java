package com.zion.zion_center.dto.lesson;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record LessonResponse(
        Long id,
        Long classId,
        String classTitle,
        Long categoryId,
        String categoryName,
        String title,
        String description,
        String youtubeUrl,
        String youtubeVideoId,
        Integer durationSeconds,
        @JsonProperty("isActive") boolean isActive,
        LocalDateTime createdAt
) {}
