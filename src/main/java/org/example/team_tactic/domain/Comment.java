package org.example.team_tactic.domain;

import java.time.Instant;

/**
 * A comment on a task. Author is identified by userId.
 */
public final class Comment {

    private final Long id;
    private final Long taskId;
    private final Long userId;
    private final String body;
    private final Instant createdAt;

    public Comment(Long id, Long taskId, Long userId, String body, Instant createdAt) {
        this.id = id;
        this.taskId = taskId;
        this.userId = userId;
        this.body = body;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getTaskId() { return taskId; }
    public Long getUserId() { return userId; }
    public String getBody() { return body; }
    public Instant getCreatedAt() { return createdAt; }
}
