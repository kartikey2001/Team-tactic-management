package org.example.team_tactic.api.controller;

import jakarta.validation.Valid;
import org.example.team_tactic.api.dto.AssignTaskRequest;
import org.example.team_tactic.api.dto.CreateTaskRequest;
import org.example.team_tactic.api.dto.TaskResponse;
import org.example.team_tactic.api.dto.UpdateTaskRequest;
import org.example.team_tactic.application.service.AssignTaskService;
import org.example.team_tactic.application.service.CreateTaskService;
import org.example.team_tactic.application.service.DeleteTaskService;
import org.example.team_tactic.application.service.ListTasksService;
import org.example.team_tactic.application.service.UpdateTaskService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final CreateTaskService createTaskService;
    private final ListTasksService listTasksService;
    private final UpdateTaskService updateTaskService;
    private final DeleteTaskService deleteTaskService;
    private final AssignTaskService assignTaskService;

    public TaskController(CreateTaskService createTaskService, ListTasksService listTasksService,
                          UpdateTaskService updateTaskService, DeleteTaskService deleteTaskService,
                          AssignTaskService assignTaskService) {
        this.createTaskService = createTaskService;
        this.listTasksService = listTasksService;
        this.updateTaskService = updateTaskService;
        this.deleteTaskService = deleteTaskService;
        this.assignTaskService = assignTaskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@CurrentUserId Long userId, @Valid @RequestBody CreateTaskRequest request) {
        Task task = createTaskService.create(
                request.title(),
                request.description(),
                request.dueDate(),
                userId
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(task));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> list(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "true") boolean desc) {
        List<Task> tasks = listTasksService.list(assigneeId, status, q, page, size, sort, desc);
        return ResponseEntity.ok(tasks.stream().map(TaskController::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(@CurrentUserId Long userId, @PathVariable Long id) {
        Task task = listTasksService.getById(id).orElseThrow(() -> new UpdateTaskService.TaskNotFoundException(id));
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
                request.dueDate()
        );
        return ResponseEntity.ok(toResponse(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@CurrentUserId Long userId, @PathVariable Long id) {
        deleteTaskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<TaskResponse> assign(@CurrentUserId Long userId, @PathVariable Long id,
                                               @Valid @RequestBody AssignTaskRequest request) {
        Task task = assignTaskService.assign(id, request.assigneeId());
        return ResponseEntity.ok(toResponse(task));
    }

    private static TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate(),
                task.getAssigneeId(),
                task.getCreatedById(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
