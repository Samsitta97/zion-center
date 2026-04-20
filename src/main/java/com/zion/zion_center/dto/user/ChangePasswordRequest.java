package com.zion.zion_center.dto.user;

import com.zion.zion_center.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank String oldPassword,
        @NotBlank @ValidPassword String newPassword
) {}
