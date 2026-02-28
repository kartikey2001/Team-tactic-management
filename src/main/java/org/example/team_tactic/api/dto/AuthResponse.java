package org.example.team_tactic.api.dto;

import java.time.Instant;

public record AuthResponse(
        String token,
        String email,
        String displayName,
        Long userId,
        Instant expiresAt
) {}
