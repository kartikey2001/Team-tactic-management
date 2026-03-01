package org.example.team_tactic.api.controller;

import jakarta.validation.Valid;
import org.example.team_tactic.api.dto.AssignTaskRequest;
import org.example.team_tactic.api.dto.AttachmentResponse;
import org.example.team_tactic.api.dto.CreateCommentRequest;
import org.example.team_tactic.api.dto.CreateTaskRequest;
import org.example.team_tactic.api.dto.CommentResponse;
import org.example.team_tactic.api.dto.TaskResponse;
import org.example.team_tactic.api.dto.UpdateTaskRequest;
import org.example.team_tactic.application.service.AssignTaskService;
import org.example.team_tactic.application.service.CreateAttachmentService;
import org.example.team_tactic.application.service.CreateCommentService;
import org.example.team_tactic.application.service.CreateTaskService;
import org.example.team_tactic.application.service.DeleteCommentService;
import org.example.team_tactic.application.service.DeleteTaskService;
import org.example.team_tactic.application.service.ListAttachmentsService;
import org.example.team_tactic.application.service.ListCommentsService;
import org.example.team_tactic.application.service.ListTasksService;
import org.example.team_tactic.application.service.UpdateTaskService;
import org.example.team_tactic.domain.Attachment;
import org.example.team_tactic.domain.Comment;
import org.example.team_tactic.domain.Task;
import org.example.team_tactic.domain.TaskStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final CreateTaskService createTaskService;
    private final ListTasksService listTasksService;
    private final UpdateTaskService updateTaskService;
    private final DeleteTaskService deleteTaskService;
    private final AssignTaskService assignTaskService;
    private final CreateCommentService createCommentService;
    private final ListCommentsService listCommentsService;
    private final DeleteCommentService deleteCommentService;
    private final CreateAttachmentService createAttachmentService;
    private final ListAttachmentsService listAttachmentsService;

    public TaskController(CreateTaskService createTaskService, ListTasksService listTasksService,
                          UpdateTaskService updateTaskService, DeleteTaskService deleteTaskService,
                          AssignTaskService assignTaskService, CreateCommentService createCommentService,
                          ListCommentsService listCommentsService, DeleteCommentService deleteCommentService,
                          CreateAttachmentService createAttachmentService,
                          ListAttachmentsService listAttachmentsService) {
        this.createTaskService = createTaskService;
        this.listTasksService = listTasksService;
        this.updateTaskService = updateTaskService;
        this.deleteTaskService = deleteTaskService;
        this.assignTaskService = assignTaskService;
        this.createCommentService = createCommentService;
        this.listCommentsService = listCommentsService;
        this.deleteCommentService = deleteCommentService;
        this.createAttachmentService = createAttachmentService;
        this.listAttachmentsService = listAttachmentsService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@CurrentUserId Long userId, @Valid @RequestBody CreateTaskRequest request) {
        Task task = createTaskService.create(
                request.title(),
                request.description(),
                request.dueDate(),
                userId,
                request.teamId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(task));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> list(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "true") boolean desc) {
        List<Task> tasks = listTasksService.list(teamId, assigneeId, status, q, page, size, sort, desc, userId);
        return ResponseEntity.ok(tasks.stream().map(TaskController::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(@CurrentUserId Long userId, @PathVariable Long id) {
        Task task = listTasksService.getByIdAndEnsureAccess(id, userId);
        return ResponseEntity.ok(toResponse(task));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@CurrentUserId Long userId, @PathVariable Long id,
                                                @Valid @RequestBody UpdateTaskRequest request) {
        Task task = updateTaskService.update(
                id,
                request.title(),
                request.description(),
                request.status(),
                request.dueDate(),
                userId
        );
        return ResponseEntity.ok(toResponse(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@CurrentUserId Long userId, @PathVariable Long id) {
        deleteTaskService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<TaskResponse> assign(@CurrentUserId Long userId, @PathVariable Long id,
                                               @Valid @RequestBody AssignTaskRequest request) {
        Task task = assignTaskService.assign(id, request.assigneeId(), userId);
        return ResponseEntity.ok(toResponse(task));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> createComment(@CurrentUserId Long userId, @PathVariable Long id,
                                                         @Valid @RequestBody CreateCommentRequest request) {
        Comment comment = createCommentService.create(id, request.body(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toCommentResponse(comment));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> listComments(@CurrentUserId Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(
                listCommentsService.listByTaskId(id, userId).stream().map(TaskController::toCommentResponse).toList());
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@CurrentUserId Long userId, @PathVariable Long id,
                                             @PathVariable Long commentId) {
        deleteCommentService.delete(commentId, id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/attachments")
    public ResponseEntity<AttachmentResponse> uploadAttachment(@CurrentUserId Long userId, @PathVariable Long id,
                                                              @RequestParam("file") MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            Attachment attachment = createAttachmentService.create(
                    id, file.getOriginalFilename(), file.getContentType(), file.getSize(),
                    inputStream, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(toAttachmentResponse(attachment));
        }
    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<AttachmentResponse>> listAttachments(@CurrentUserId Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(
                listAttachmentsService.listByTaskId(id, userId).stream()
                        .map(TaskController::toAttachmentResponse)
                        .toList());
    }

    private static AttachmentResponse toAttachmentResponse(Attachment a) {
        return new AttachmentResponse(
                a.getId(), a.getTaskId(), a.getUserId(), a.getFileName(),
                a.getContentType(), a.getSize(), a.getCreatedAt());
    }

    private static CommentResponse toCommentResponse(Comment c) {
        return new CommentResponse(c.getId(), c.getTaskId(), c.getUserId(), c.getBody(), c.getCreatedAt());
    }

    private static TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate(),
                task.getTeamId(),
                task.getAssigneeId(),
                task.getCreatedById(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
