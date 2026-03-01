package org.example.team_tactic.api.dto;

import java.time.Instant;

public record TeamResponse(
        Long id,
        String name,
        String description,
        Long createdById,
        Instant createdAt
) {}
