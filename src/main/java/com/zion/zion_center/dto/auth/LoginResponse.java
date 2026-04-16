package com.zion.zion_center.dto.auth;

public record LoginResponse(
        String token,
        String name,
        String email,
        String role
) {}
