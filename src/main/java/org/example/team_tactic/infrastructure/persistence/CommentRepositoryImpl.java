package org.example.team_tactic.infrastructure.persistence;

import org.example.team_tactic.application.port.CommentRepository;
import org.example.team_tactic.domain.Comment;
import org.example.team_tactic.infrastructure.persistence.entity.CommentEntity;
import org.example.team_tactic.infrastructure.persistence.repository.CommentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentJpaRepository jpaRepository;

    public CommentRepositoryImpl(CommentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Comment save(Comment comment) {
        CommentEntity entity = toEntity(comment);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Comment> findByTaskIdOrderByCreatedAtAsc(Long taskId) {
        return jpaRepository.findByTaskIdOrderByCreatedAtAsc(taskId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Comment toDomain(CommentEntity e) {
        return new Comment(e.getId(), e.getTaskId(), e.getUserId(), e.getBody(), e.getCreatedAt());
    }

    private CommentEntity toEntity(Comment c) {
        CommentEntity e = new CommentEntity();
        if (c.getId() != null) e.setId(c.getId());
        e.setTaskId(c.getTaskId());
        e.setUserId(c.getUserId());
        e.setBody(c.getBody());
        e.setCreatedAt(c.getCreatedAt());
        return e;
    }
}
