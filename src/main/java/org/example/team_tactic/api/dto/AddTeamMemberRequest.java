package org.example.team_tactic.api.dto;

import jakarta.validation.constraints.NotNull;

/** Add a user to the team by user ID. */
public record AddTeamMemberRequest(
        @NotNull(message = "User ID is required")
        Long userId
) {}
