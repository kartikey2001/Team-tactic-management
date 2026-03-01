package org.example.team_tactic.api.dto;

import jakarta.validation.constraints.Size;
import org.example.team_tactic.domain.TaskStatus;

import java.time.Instant;

public record UpdateTaskRequest(
        @Size(max = 500)
        String title,

        @Size(max = 5000)
        String description,

        TaskStatus status,

        Instant dueDate
) {}
