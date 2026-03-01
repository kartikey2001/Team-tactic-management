package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.CommentRepository;
import org.example.team_tactic.domain.Comment;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CreateCommentService {

    private final CommentRepository commentRepository;
    private final ListTasksService listTasksService;

    public CreateCommentService(CommentRepository commentRepository, ListTasksService listTasksService) {
        this.commentRepository = commentRepository;
        this.listTasksService = listTasksService;
    }

    /**
     * Creates a comment on a task. Caller must have access to the task (owner or team member).
     */
    public Comment create(Long taskId, String body, Long userId) {
        listTasksService.getByIdAndEnsureAccess(taskId, userId);
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Comment body is required");
        }
        Comment comment = new Comment(null, taskId, userId, body.trim(), Instant.now());
        return commentRepository.save(comment);
    }
}
