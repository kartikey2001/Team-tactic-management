package org.example.team_tactic.infrastructure.persistence.repository;

import org.example.team_tactic.infrastructure.persistence.entity.AttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentJpaRepository extends JpaRepository<AttachmentEntity, Long> {

    List<AttachmentEntity> findByTaskIdOrderByCreatedAtAsc(Long taskId);
}
