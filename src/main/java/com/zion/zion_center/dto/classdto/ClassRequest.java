package com.zion.zion_center.dto.classdto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record ClassRequest(
        @NotBlank String title,
        String description,
        Long categoryId,
        LocalDate classDate
) {}
