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

    public ListTasksService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> list(Long assigneeId, TaskStatus status,
                           String searchTerm, int page, int size, String sortBy, boolean sortDesc) {
        return taskRepository.findAll(assigneeId, status, searchTerm, page, size, sortBy, sortDesc);
    }

    public Optional<Task> getById(Long taskId) {
        return taskRepository.findById(taskId);
    }
}
