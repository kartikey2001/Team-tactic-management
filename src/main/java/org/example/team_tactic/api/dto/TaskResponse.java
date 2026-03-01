package org.example.team_tactic.api.dto;

import org.example.team_tactic.domain.TaskStatus;

import java.time.Instant;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        Instant dueDate,
        Long assigneeId,
        Long createdById,
        Instant createdAt,
        Instant updatedAt
) {}
