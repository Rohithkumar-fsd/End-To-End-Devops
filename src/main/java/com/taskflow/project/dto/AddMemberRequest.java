package com.taskflow.project.dto;

import com.taskflow.project.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddMemberRequest(
        @NotBlank(message = "User email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "Role is required (ADMIN or MEMBER)")
        Role role
) {}