package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.AttachmentRepository;
import org.example.team_tactic.application.port.FileStorage;
import org.example.team_tactic.domain.Attachment;
import org.example.team_tactic.domain.Task;
import org.example.team_tactic.domain.TeamMember;
import org.example.team_tactic.domain.TeamRole;
import org.example.team_tactic.application.port.TaskRepository;
import org.example.team_tactic.application.port.TeamMemberRepository;
import org.springframework.stereotype.Service;

/**
 * Delete an attachment. Allowed if the caller is the uploader, or a team OWNER for the task's team.
 */
@Service
public class DeleteAttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final FileStorage fileStorage;
    private final TaskRepository taskRepository;
    private final ListTasksService listTasksService;
    private final TeamMemberRepository teamMemberRepository;

    public DeleteAttachmentService(AttachmentRepository attachmentRepository,
                                  FileStorage fileStorage,
                                  TaskRepository taskRepository,
                                  ListTasksService listTasksService,
                                  TeamMemberRepository teamMemberRepository) {
        this.attachmentRepository = attachmentRepository;
        this.fileStorage = fileStorage;
        this.taskRepository = taskRepository;
        this.listTasksService = listTasksService;
        this.teamMemberRepository = teamMemberRepository;
    }

    public void delete(Long attachmentId, Long requestingUserId) {
        delete(attachmentId, null, requestingUserId);
    }

    public void delete(Long attachmentId, Long taskId, Long requestingUserId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException(attachmentId));
        if (taskId != null && !attachment.getTaskId().equals(taskId)) {
            throw new AttachmentNotFoundException(attachmentId);
        }
        listTasksService.getByIdAndEnsureAccess(attachment.getTaskId(), requestingUserId);

        if (attachment.getUserId().equals(requestingUserId)) {
            deleteAttachmentAndFile(attachment);
            return;
        }
        Task task = taskRepository.findById(attachment.getTaskId())
                .orElseThrow(() -> new UpdateTaskService.TaskNotFoundException(attachment.getTaskId()));
        if (task.getTeamId() != null) {
            TeamMember requesterMember = teamMemberRepository.findByTeamIdAndUserId(task.getTeamId(), requestingUserId)
                    .orElse(null);
            if (requesterMember != null && requesterMember.getRole() == TeamRole.OWNER) {
                deleteAttachmentAndFile(attachment);
                return;
            }
        }
        throw new ForbiddenToDeleteAttachmentException(attachmentId);
    }

    private void deleteAttachmentAndFile(Attachment attachment) {
        try {
            fileStorage.delete(attachment.getStoredPath());
        } catch (Exception e) {
            // Log but still remove DB record to avoid orphan references
        }
        attachmentRepository.deleteById(attachment.getId());
    }

    public static final class AttachmentNotFoundException extends RuntimeException {
        public AttachmentNotFoundException(Long attachmentId) {
            super("Attachment not found: " + attachmentId);
        }
    }

    public static final class ForbiddenToDeleteAttachmentException extends RuntimeException {
        public ForbiddenToDeleteAttachmentException(Long attachmentId) {
            super("Not allowed to delete attachment: " + attachmentId);
        }
    }
}
