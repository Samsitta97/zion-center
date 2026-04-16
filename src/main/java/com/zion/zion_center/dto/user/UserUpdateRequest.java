package com.zion.zion_center.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotNull String role,
        boolean active
) {}
