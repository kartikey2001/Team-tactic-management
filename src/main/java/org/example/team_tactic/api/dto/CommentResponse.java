package org.example.team_tactic.api.dto;

import java.time.Instant;

public record CommentResponse(
        Long id,
        Long taskId,
        Long userId,
        String body,
        Instant createdAt
) {}
