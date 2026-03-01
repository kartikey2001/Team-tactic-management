package org.example.team_tactic.infrastructure.persistence;

import org.example.team_tactic.application.port.TaskRepository;
import org.example.team_tactic.domain.Task;
import org.example.team_tactic.domain.TaskStatus;
import org.example.team_tactic.infrastructure.persistence.entity.TaskEntity;
import org.example.team_tactic.infrastructure.persistence.repository.TaskJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TaskRepositoryImpl implements TaskRepository {

    private final TaskJpaRepository jpaRepository;

    public TaskRepositoryImpl(TaskJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Task save(Task task) {
        TaskEntity entity = toEntity(task);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Task> findAll(Long teamId, Long assigneeId, TaskStatus status, String searchTerm, int page, int size, String sortBy, boolean sortDesc) {
        Specification<TaskEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (teamId != null) {
                predicates.add(cb.equal(root.get("teamId"), teamId));
            }
            if (assigneeId != null) {
                predicates.add(cb.equal(root.get("assigneeId"), assigneeId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (searchTerm != null && !searchTerm.isBlank()) {
                String pattern = "%" + searchTerm.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        String orderBy = (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt";
        var sort = sortDesc
                ? org.springframework.data.domain.Sort.by(orderBy).descending()
                : org.springframework.data.domain.Sort.by(orderBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return jpaRepository.findAll(spec, pageable).stream().map(this::toDomain).toList();
    }

    private Task toDomain(TaskEntity e) {
        return new Task(
                e.getId(), e.getTitle(), e.getDescription(), e.getStatus(), e.getDueDate(),
                e.getTeamId(), e.getAssigneeId(), e.getCreatedById(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }

    private TaskEntity toEntity(Task t) {
        TaskEntity e = new TaskEntity();
        if (t.getId() != null) e.setId(t.getId());
        e.setTitle(t.getTitle());
        e.setDescription(t.getDescription());
        e.setStatus(t.getStatus());
        e.setDueDate(t.getDueDate());
        e.setTeamId(t.getTeamId());
        e.setAssigneeId(t.getAssigneeId());
        e.setCreatedById(t.getCreatedById());
        e.setCreatedAt(t.getCreatedAt());
        e.setUpdatedAt(t.getUpdatedAt());
        return e;
    }
}
