package org.example.team_tactic.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotBlank(message = "Comment body is required")
        @Size(max = 5000)
        String body
) {}
