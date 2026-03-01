package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.CommentRepository;
import org.example.team_tactic.domain.Comment;
import org.example.team_tactic.domain.Task;
import org.example.team_tactic.domain.TeamMember;
import org.example.team_tactic.domain.TeamRole;
import org.example.team_tactic.application.port.TaskRepository;
import org.example.team_tactic.application.port.TeamMemberRepository;
import org.springframework.stereotype.Service;

/**
 * Delete a comment. Allowed if the caller is the comment author, or a team OWNER for the task's team.
 */
@Service
public class DeleteCommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final ListTasksService listTasksService;
    private final TeamMemberRepository teamMemberRepository;

    public DeleteCommentService(CommentRepository commentRepository, TaskRepository taskRepository,
                                ListTasksService listTasksService, TeamMemberRepository teamMemberRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.listTasksService = listTasksService;
        this.teamMemberRepository = teamMemberRepository;
    }

    public void delete(Long commentId, Long requestingUserId) {
        delete(commentId, null, requestingUserId);
    }

    /**
     * Delete a comment, optionally ensuring it belongs to the given taskId (for URL consistency).
     */
    public void delete(Long commentId, Long taskId, Long requestingUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        if (taskId != null && !comment.getTaskId().equals(taskId)) {
            throw new CommentNotFoundException(commentId);
        }
        listTasksService.getByIdAndEnsureAccess(comment.getTaskId(), requestingUserId);

        if (comment.getUserId().equals(requestingUserId)) {
            commentRepository.deleteById(commentId);
            return;
        }
        Task task = taskRepository.findById(comment.getTaskId())
                .orElseThrow(() -> new UpdateTaskService.TaskNotFoundException(comment.getTaskId()));
        if (task.getTeamId() != null) {
            TeamMember requesterMember = teamMemberRepository.findByTeamIdAndUserId(task.getTeamId(), requestingUserId)
                    .orElse(null);
            if (requesterMember != null && requesterMember.getRole() == TeamRole.OWNER) {
                commentRepository.deleteById(commentId);
                return;
            }
        }
        throw new ForbiddenToDeleteCommentException(commentId);
    }

    public static final class CommentNotFoundException extends RuntimeException {
        public CommentNotFoundException(Long commentId) {
            super("Comment not found: " + commentId);
        }
    }

    public static final class ForbiddenToDeleteCommentException extends RuntimeException {
        public ForbiddenToDeleteCommentException(Long commentId) {
            super("Not allowed to delete comment: " + commentId);
        }
    }
}
