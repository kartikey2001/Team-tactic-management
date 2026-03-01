package org.example.team_tactic.domain;

import java.time.Instant;

/**
 * Domain task. Assignee and creator are user IDs (team scoping in Phase 4).
 */
public final class Task {

    private final Long id;
    private final String title;
    private final String description;
    private final TaskStatus status;
    private final Instant dueDate;
    private final Long teamId;
    private final Long assigneeId;
    private final Long createdById;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Task(Long id, String title, String description, TaskStatus status, Instant dueDate,
                Long teamId, Long assigneeId, Long createdById, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
        this.teamId = teamId;
        this.assigneeId = assigneeId;
        this.createdById = createdById;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public Instant getDueDate() { return dueDate; }
    public Long getTeamId() { return teamId; }
    public Long getAssigneeId() { return assigneeId; }
    public Long getCreatedById() { return createdById; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public Task withIdAndTimestamps(Long id, Instant createdAt, Instant updatedAt) {
        return new Task(id, title, description, status, dueDate, teamId, assigneeId, createdById, createdAt, updatedAt);
    }

    public Task withTitle(String newTitle) {
        return new Task(id, newTitle, description, status, dueDate, teamId, assigneeId, createdById, createdAt, updatedAt);
    }

    public Task withDescription(String newDescription) {
        return new Task(id, title, newDescription, status, dueDate, teamId, assigneeId, createdById, createdAt, updatedAt);
    }

    public Task withStatus(TaskStatus newStatus) {
        return new Task(id, title, description, newStatus, dueDate, teamId, assigneeId, createdById, createdAt, updatedAt);
    }

    public Task withDueDate(Instant newDueDate) {
        return new Task(id, title, description, status, newDueDate, teamId, assigneeId, createdById, createdAt, updatedAt);
    }

    public Task withAssigneeId(Long newAssigneeId) {
        return new Task(id, title, description, status, dueDate, teamId, newAssigneeId, createdById, createdAt, updatedAt);
    }

    public Task withUpdatedAt(Instant newUpdatedAt) {
        return new Task(id, title, description, status, dueDate, teamId, assigneeId, createdById, createdAt, newUpdatedAt);
    }
}
