package com.zion.zion_center.dto.lesson;

import java.time.LocalDateTime;

public record LessonResponse(
        Long id,
        Long classId,
        String classTitle,
        String title,
        String description,
        String youtubeUrl,
        String youtubeVideoId,
        Integer durationSeconds,
        boolean isActive,
        LocalDateTime createdAt
) {}
