package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.TaskRepository;
import org.example.team_tactic.application.port.UserRepository;
import org.example.team_tactic.domain.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignTaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public AssignTaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Task assign(Long taskId, Long assigneeId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new UpdateTaskService.TaskNotFoundException(taskId));
        if (assigneeId != null && userRepository.findById(assigneeId).isEmpty()) {
            throw new AssigneeNotFoundException(assigneeId);
        }
        task = task.withAssigneeId(assigneeId).withUpdatedAt(java.time.Instant.now());
        return taskRepository.save(task);
    }

    public static final class AssigneeNotFoundException extends RuntimeException {
        public AssigneeNotFoundException(Long userId) {
            super("User not found: " + userId);
        }
    }
}
