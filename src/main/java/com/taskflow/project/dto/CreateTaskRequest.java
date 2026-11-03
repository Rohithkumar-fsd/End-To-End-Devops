package com.taskflow.project.dto;

import com.taskflow.project.model.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.taskflow.project.model.TaskStatus;

public record CreateTaskRequest(
        @NotBlank(message = "Task title is required")
        @Size(max = 150, message = "Title cannot exceed 150 characters")
        String title,

        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        String description,

        TaskStatus status,
        TaskPriority priority,

        @NotNull(message = "Project ID is required")
        Long projectId,

        Long assignedToUserId
) {}