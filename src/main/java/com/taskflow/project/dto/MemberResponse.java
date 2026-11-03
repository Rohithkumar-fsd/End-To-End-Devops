package com.taskflow.project.dto;

import com.taskflow.project.model.Role;
import java.time.LocalDateTime;

public record MemberResponse(
        Long id,
        Long userId,
        String name,
        String email,
        Role role,
        LocalDateTime joinedAt
) {}