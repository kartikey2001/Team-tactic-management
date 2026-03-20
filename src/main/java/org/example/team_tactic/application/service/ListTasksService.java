package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.TaskRepository;
import org.example.team_tactic.domain.Task;
import org.example.team_tactic.domain.TaskStatus;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ListTasksService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt",
            "updatedAt",
            "dueDate",
            "title",
            "status"
    );

    private final TaskRepository taskRepository;
    private final GetTeamService getTeamService;

    public ListTasksService(TaskRepository taskRepository, GetTeamService getTeamService) {
        this.taskRepository = taskRepository;
        this.getTeamService = getTeamService;
    }

    public List<Task> list(Long teamId, Long assigneeId, TaskStatus status,
                           String searchTerm, int page, int size, String sortBy, boolean sortDesc, Long requestingUserId) {
        if (teamId != null && !getTeamService.isMember(teamId, requestingUserId)) {
            throw new AddTeamMemberService.NotTeamMemberException(teamId, requestingUserId);
        }
        String normalizedSortBy = normalizeAndValidateSortBy(sortBy);
        return taskRepository.findAll(teamId, assigneeId, status, searchTerm, page, size, normalizedSortBy, sortDesc);
    }

    public Optional<Task> getById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    /** Throws if task has a team and user is not a member. */
    public Task getByIdAndEnsureAccess(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new UpdateTaskService.TaskNotFoundException(taskId));
        if (task.getTeamId() != null && !getTeamService.isMember(task.getTeamId(), userId)) {
            throw new AddTeamMemberService.NotTeamMemberException(task.getTeamId(), userId);
        }
        return task;
    }

    private String normalizeAndValidateSortBy(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "createdAt";
        }
        String trimmed = sortBy.trim();
        String normalized = trimmed.substring(0, 1).toLowerCase(Locale.ROOT) + trimmed.substring(1);
        if (!ALLOWED_SORT_FIELDS.contains(normalized)) {
            throw new IllegalArgumentException("Invalid sort field: " + sortBy +
                    ". Allowed values are: " + String.join(", ", ALLOWED_SORT_FIELDS));
        }
        return normalized;
    }
}
