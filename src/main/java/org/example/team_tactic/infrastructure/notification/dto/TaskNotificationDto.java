package org.example.team_tactic.infrastructure.notification.dto;

import org.example.team_tactic.domain.Task;
import org.example.team_tactic.domain.TaskStatus;

import java.time.Instant;

/**
 * Stable transport DTO for task data in notification payloads.
 */
public record TaskNotificationDto(
        Long id,
        String title,
        String description,
        TaskStatus status,
        Instant dueDate,
        Long teamId,
        Long assigneeId,
        Long createdById,
        Instant createdAt,
        Instant updatedAt
) {
    public static TaskNotificationDto from(Task task) {
        return new TaskNotificationDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate(),
                task.getTeamId(),
                task.getAssigneeId(),
                task.getCreatedById(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
