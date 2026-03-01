package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.NotificationService;
import org.example.team_tactic.application.port.TaskRepository;
import org.example.team_tactic.domain.Task;
import org.example.team_tactic.domain.TaskStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UpdateTaskService {

    private final TaskRepository taskRepository;
    private final GetTeamService getTeamService;
    private final NotificationService notificationService;

    public UpdateTaskService(TaskRepository taskRepository, GetTeamService getTeamService,
                             NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.getTeamService = getTeamService;
        this.notificationService = notificationService;
    }

    @Transactional
    public Task update(Long taskId, String title, String description, TaskStatus status, Instant dueDate, Long requestingUserId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        if (task.getTeamId() != null && !getTeamService.isMember(task.getTeamId(), requestingUserId)) {
            throw new AddTeamMemberService.NotTeamMemberException(task.getTeamId(), requestingUserId);
        }
        if (title != null) {
            task = task.withTitle(title);
        }
        if (description != null) {
            task = task.withDescription(description);
        }
        if (status != null) {
            task = task.withStatus(status);
        }
        if (dueDate != null) {
            task = task.withDueDate(dueDate);
        }
        task = task.withUpdatedAt(Instant.now());
        task = taskRepository.save(task);
        if (task.getAssigneeId() != null) {
            notificationService.publishTaskUpdated(task.getAssigneeId(), task);
        }
        return task;
    }

    public static final class TaskNotFoundException extends RuntimeException {
        public TaskNotFoundException(Long taskId) {
            super("Task not found: " + taskId);
        }
    }
}
