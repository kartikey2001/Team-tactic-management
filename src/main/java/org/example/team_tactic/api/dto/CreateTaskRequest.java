package org.example.team_tactic.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateTaskRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 500)
        String title,

        @Size(max = 5000)
        String description,

        Instant dueDate,

        /** Optional: scope task to a team (caller must be a member). */
        Long teamId
) {}
