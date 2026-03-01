package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.TaskRepository;
import org.example.team_tactic.domain.Task;
import org.example.team_tactic.domain.TaskStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UpdateTaskService {

    private final TaskRepository taskRepository;

    public UpdateTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Task update(Long taskId, String title, String description, TaskStatus status, Instant dueDate) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
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
        return taskRepository.save(task);
    }

    public static final class TaskNotFoundException extends RuntimeException {
        public TaskNotFoundException(Long taskId) {
            super("Task not found: " + taskId);
        }
    }
}
