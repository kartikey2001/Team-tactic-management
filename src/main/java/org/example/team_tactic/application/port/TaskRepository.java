package org.example.team_tactic.application.port;

import org.example.team_tactic.domain.Task;
import org.example.team_tactic.domain.TaskStatus;

import java.util.List;
import java.util.Optional;

/**
 * Port for task persistence.
 */
public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(Long id);

    void deleteById(Long id);

    boolean existsById(Long id);

    /**
     * Find tasks with optional filters. Pass null for assigneeId/status to ignore.
     * searchTerm matches title/description (case-insensitive contains); null = no search.
     */
    List<Task> findAll(Long assigneeId, TaskStatus status, String searchTerm, int page, int size, String sortBy, boolean sortDesc);
}
