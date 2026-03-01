package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.TaskRepository;
import org.example.team_tactic.domain.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteTaskService {

    private final TaskRepository taskRepository;
    private final GetTeamService getTeamService;

    public DeleteTaskService(TaskRepository taskRepository, GetTeamService getTeamService) {
        this.taskRepository = taskRepository;
        this.getTeamService = getTeamService;
    }

    @Transactional
    public void delete(Long taskId, Long requestingUserId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new UpdateTaskService.TaskNotFoundException(taskId));
        if (task.getTeamId() != null && !getTeamService.isMember(task.getTeamId(), requestingUserId)) {
            throw new AddTeamMemberService.NotTeamMemberException(task.getTeamId(), requestingUserId);
        }
        taskRepository.deleteById(taskId);
    }
}
