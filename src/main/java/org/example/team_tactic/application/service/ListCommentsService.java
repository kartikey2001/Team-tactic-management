package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.CommentRepository;
import org.example.team_tactic.domain.Comment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListCommentsService {

    private final CommentRepository commentRepository;
    private final ListTasksService listTasksService;

    public ListCommentsService(CommentRepository commentRepository, ListTasksService listTasksService) {
        this.commentRepository = commentRepository;
        this.listTasksService = listTasksService;
    }

    /**
     * Lists comments for a task. Caller must have access to the task.
     */
    public List<Comment> listByTaskId(Long taskId, Long requestingUserId) {
        listTasksService.getByIdAndEnsureAccess(taskId, requestingUserId);
        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);
    }
}
