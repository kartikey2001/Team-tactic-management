package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.AttachmentRepository;
import org.example.team_tactic.application.port.FileStorage;
import org.example.team_tactic.domain.Attachment;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;

@Service
public class CreateAttachmentService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final String STORAGE_PREFIX = "attachments/";

    private final AttachmentRepository attachmentRepository;
    private final FileStorage fileStorage;
    private final ListTasksService listTasksService;

    public CreateAttachmentService(AttachmentRepository attachmentRepository,
                                   FileStorage fileStorage,
                                   ListTasksService listTasksService) {
        this.attachmentRepository = attachmentRepository;
        this.fileStorage = fileStorage;
        this.listTasksService = listTasksService;
    }

    /**
     * Uploads an attachment for a task. Caller must have access to the task.
     */
    public Attachment create(Long taskId, String originalFileName, String contentType, long size,
                            InputStream inputStream, Long userId) {
        listTasksService.getByIdAndEnsureAccess(taskId, userId);
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("File name is required");
        }
        if (size > MAX_FILE_SIZE) {
            throw new FileTooLargeException(size, MAX_FILE_SIZE);
        }
        String safeFileName = sanitizeFileName(originalFileName);
        String relativePath = STORAGE_PREFIX + UUID.randomUUID() + "-" + safeFileName;
        String storedPath = fileStorage.store(inputStream, relativePath);
        Attachment attachment = new Attachment(
                null, taskId, userId, originalFileName, storedPath,
                contentType != null ? contentType : "application/octet-stream",
                size, Instant.now()
        );
        return attachmentRepository.save(attachment);
    }

    private static String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    public static final class FileTooLargeException extends RuntimeException {
        public FileTooLargeException(long size, long max) {
            super("File size " + size + " exceeds maximum " + max + " bytes");
        }
    }
}
