package org.example.team_tactic.api.controller;

import jakarta.validation.Valid;
import org.example.team_tactic.api.dto.CommentResponse;
import org.example.team_tactic.api.dto.CreateCommentRequest;
import org.example.team_tactic.application.service.CreateCommentService;
import org.example.team_tactic.application.service.DeleteCommentService;
import org.example.team_tactic.application.service.ListCommentsService;
import org.example.team_tactic.domain.Comment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/comments")
public class TaskCommentController {

    private final CreateCommentService createCommentService;
    private final ListCommentsService listCommentsService;
    private final DeleteCommentService deleteCommentService;

    public TaskCommentController(CreateCommentService createCommentService,
                                 ListCommentsService listCommentsService,
                                 DeleteCommentService deleteCommentService) {
        this.createCommentService = createCommentService;
        this.listCommentsService = listCommentsService;
        this.deleteCommentService = deleteCommentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@CurrentUserId Long userId,
                                                         @PathVariable Long taskId,
                                                         @Valid @RequestBody CreateCommentRequest request) {
        Comment comment = createCommentService.create(taskId, request.body(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toCommentResponse(comment));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> listComments(@CurrentUserId Long userId, @PathVariable Long taskId) {
        return ResponseEntity.ok(
                listCommentsService.listByTaskId(taskId, userId).stream()
                        .map(TaskCommentController::toCommentResponse)
                        .toList());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@CurrentUserId Long userId,
                                              @PathVariable Long taskId,
                                              @PathVariable Long commentId) {
        deleteCommentService.delete(commentId, taskId, userId);
        return ResponseEntity.noContent().build();
    }

    private static CommentResponse toCommentResponse(Comment c) {
        return new CommentResponse(c.getId(), c.getTaskId(), c.getUserId(), c.getBody(), c.getCreatedAt());
    }
}
