package com.zion.zion_center.dto.user;

import com.zion.zion_center.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank @ValidPassword String password,
        @NotNull String role
) {}
