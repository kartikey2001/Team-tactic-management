package org.example.team_tactic.api.dto;

import java.time.Instant;

public record AttachmentResponse(
        Long id,
        Long taskId,
        Long userId,
        String fileName,
        String contentType,
        long size,
        Instant createdAt
) {}
