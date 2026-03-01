package org.example.team_tactic.infrastructure.persistence.repository;

import org.example.team_tactic.infrastructure.persistence.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findByTaskIdOrderByCreatedAtAsc(Long taskId);
}
