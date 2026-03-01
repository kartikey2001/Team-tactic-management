package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.AttachmentRepository;
import org.example.team_tactic.application.port.FileStorage;
import org.example.team_tactic.domain.Attachment;
import org.springframework.stereotype.Service;

import java.io.OutputStream;

@Service
public class GetAttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final FileStorage fileStorage;
    private final ListTasksService listTasksService;

    public GetAttachmentService(AttachmentRepository attachmentRepository,
                                FileStorage fileStorage,
                                ListTasksService listTasksService) {
        this.attachmentRepository = attachmentRepository;
        this.fileStorage = fileStorage;
        this.listTasksService = listTasksService;
    }

    /**
     * Returns attachment metadata and writes file content to output. Caller must have access to the task.
     */
    public Attachment getByIdAndWriteContent(Long attachmentId, Long requestingUserId, OutputStream output) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException(attachmentId));
        listTasksService.getByIdAndEnsureAccess(attachment.getTaskId(), requestingUserId);
        fileStorage.read(attachment.getStoredPath(), output);
        return attachment;
    }

    /**
     * Returns attachment metadata only (for validation/authorization).
     */
    public Attachment getById(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException(attachmentId));
    }

    public static final class AttachmentNotFoundException extends RuntimeException {
        public AttachmentNotFoundException(Long attachmentId) {
            super("Attachment not found: " + attachmentId);
        }
    }
}
