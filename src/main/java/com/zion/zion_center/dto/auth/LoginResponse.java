package com.zion.zion_center.dto.auth;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String name,
        String email,
        String role
) {}
