package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.TaskRepository;
import org.example.team_tactic.domain.Task;
import org.example.team_tactic.domain.TaskStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ListTasksService {

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
        return taskRepository.findAll(teamId, assigneeId, status, searchTerm, page, size, sortBy, sortDesc);
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
}
