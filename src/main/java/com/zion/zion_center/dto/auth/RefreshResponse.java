package com.zion.zion_center.dto.auth;

public record RefreshResponse(
        String accessToken,
        String refreshToken
) {}
