package com.zion.zion_center.dto.sharedlink;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record SharedLinkResponse(
        Long id,
        String token,
        String watchUrl,
        Long lessonId,
        String lessonTitle,
        LocalDateTime expiresAt,
        Integer maxViews,
        int viewCount,
        @JsonProperty("isActive") boolean isActive,
        LocalDateTime createdAt
) {}
