package com.taskflow.project.dto;

import com.taskflow.project.model.TaskPriority;
import com.taskflow.project.model.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Long projectId,
        String assignedToUserName,
        LocalDateTime createdAt,
        Long assignedToUserId
) {}