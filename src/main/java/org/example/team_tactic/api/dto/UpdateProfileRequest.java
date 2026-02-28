package org.example.team_tactic.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "Display name is required")
        @Size(max = 255)
        String displayName
) {}
