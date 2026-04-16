package com.zion.zion_center.dto.classdto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClassResponse(
        Long id,
        String title,
        String description,
        String categoryName,
        LocalDate classDate,
        String status,
        LocalDateTime createdAt
) {}
