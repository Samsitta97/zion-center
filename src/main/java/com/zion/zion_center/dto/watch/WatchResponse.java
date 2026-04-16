package com.zion.zion_center.dto.watch;

public record WatchResponse(
        String lessonTitle,
        String classTitle,
        String youtubeUrl,
        String youtubeVideoId,
        String description
) {}
