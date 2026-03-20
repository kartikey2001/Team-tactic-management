package org.example.team_tactic.infrastructure.notification.dto;

import java.time.Instant;

/**
 * SSE notification envelope with explicit event metadata.
 */
public record NotificationEventDto(
        String type,
        Instant timestamp,
        TaskNotificationDto task
) {
}
