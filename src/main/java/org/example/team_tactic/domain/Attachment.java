package org.example.team_tactic.domain;

import java.time.Instant;

/**
 * An attachment on a task. Stored path references the physical file location.
 */
public final class Attachment {

    private final Long id;
    private final Long taskId;
    private final Long userId;
    private final String fileName;
    private final String storedPath;
    private final String contentType;
    private final long size;
    private final Instant createdAt;

    public Attachment(Long id, Long taskId, Long userId, String fileName, String storedPath,
                      String contentType, long size, Instant createdAt) {
        this.id = id;
        this.taskId = taskId;
        this.userId = userId;
        this.fileName = fileName;
        this.storedPath = storedPath;
        this.contentType = contentType;
        this.size = size;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getTaskId() { return taskId; }
    public Long getUserId() { return userId; }
    public String getFileName() { return fileName; }
    public String getStoredPath() { return storedPath; }
    public String getContentType() { return contentType; }
    public long getSize() { return size; }
    public Instant getCreatedAt() { return createdAt; }
}
