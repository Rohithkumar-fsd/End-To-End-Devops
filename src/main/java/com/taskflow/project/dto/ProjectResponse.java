
package com.taskflow.project.dto;

import java.time.LocalDateTime;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt,
        Long createdById,
        String createdByName,
        String createdByEmail
) {
}
