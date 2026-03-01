package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.TaskRepository;
import org.example.team_tactic.domain.Task;
import org.example.team_tactic.domain.TaskStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class CreateTaskService {

    private final TaskRepository taskRepository;
    private final GetTeamService getTeamService;

    public CreateTaskService(TaskRepository taskRepository, GetTeamService getTeamService) {
        this.taskRepository = taskRepository;
        this.getTeamService = getTeamService;
    }

    @Transactional
    public Task create(String title, String description, Instant dueDate, Long createdById, Long teamId) {
        if (teamId != null && !getTeamService.isMember(teamId, createdById)) {
            throw new AddTeamMemberService.NotTeamMemberException(teamId, createdById);
        }
        TaskStatus status = TaskStatus.OPEN;
        Instant now = Instant.now();
        Task task = new Task(null, title, description, status, dueDate, teamId, null, createdById, now, now);
        return taskRepository.save(task);
    }
}
