package org.example.team_tactic.api.dto;

import org.example.team_tactic.domain.TeamRole;

import java.time.Instant;

public record TeamMemberResponse(
        Long id,
        Long teamId,
        Long userId,
        TeamRole role,
        Instant joinedAt
) {}
