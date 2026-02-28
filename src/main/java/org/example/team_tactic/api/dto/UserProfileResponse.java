package org.example.team_tactic.api.dto;

import java.time.Instant;

public record UserProfileResponse(
        Long id,
        String email,
        String displayName,
        Instant createdAt
) {}
