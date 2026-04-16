package com.zion.zion_center.dto.sharedlink;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SharedLinkRequest(
        @NotNull Long lessonId,
        LocalDateTime expiresAt,
        Integer maxViews
) {}
