package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.NotificationService;
import org.example.team_tactic.application.port.TaskRepository;
import org.example.team_tactic.application.port.UserRepository;
import org.example.team_tactic.domain.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignTaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final GetTeamService getTeamService;
    private final NotificationService notificationService;

    public AssignTaskService(TaskRepository taskRepository, UserRepository userRepository,
                             GetTeamService getTeamService, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.getTeamService = getTeamService;
        this.notificationService = notificationService;
    }

    @Transactional
    public Task assign(Long taskId, Long assigneeId, Long assignedByUserId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new UpdateTaskService.TaskNotFoundException(taskId));
        if (task.getTeamId() != null && !getTeamService.isMember(task.getTeamId(), assignedByUserId)) {
            throw new AddTeamMemberService.NotTeamMemberException(task.getTeamId(), assignedByUserId);
        }
        if (assigneeId != null) {
            if (userRepository.findById(assigneeId).isEmpty()) {
                throw new AssigneeNotFoundException(assigneeId);
            }
            if (task.getTeamId() != null && !getTeamService.isMember(task.getTeamId(), assigneeId)) {
                throw new AssigneeNotTeamMemberException(task.getTeamId(), assigneeId);
            }
        }
        task = task.withAssigneeId(assigneeId).withUpdatedAt(java.time.Instant.now());
        task = taskRepository.save(task);
        if (assigneeId != null) {
            notificationService.publishTaskAssigned(assigneeId, task);
        }
        return task;
    }

    public static final class AssigneeNotFoundException extends RuntimeException {
        public AssigneeNotFoundException(Long userId) {
            super("User not found: " + userId);
        }
    }

    public static final class AssigneeNotTeamMemberException extends RuntimeException {
        public AssigneeNotTeamMemberException(Long teamId, Long userId) {
            super("User " + userId + " is not a member of team " + teamId + "; cannot assign task.");
        }
    }
}
