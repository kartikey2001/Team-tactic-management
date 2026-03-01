package org.example.team_tactic.infrastructure.persistence;

import org.example.team_tactic.application.port.AttachmentRepository;
import org.example.team_tactic.domain.Attachment;
import org.example.team_tactic.infrastructure.persistence.entity.AttachmentEntity;
import org.example.team_tactic.infrastructure.persistence.repository.AttachmentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AttachmentRepositoryImpl implements AttachmentRepository {

    private final AttachmentJpaRepository jpaRepository;

    public AttachmentRepositoryImpl(AttachmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Attachment save(Attachment attachment) {
        AttachmentEntity entity = toEntity(attachment);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Attachment> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Attachment> findByTaskIdOrderByCreatedAtAsc(Long taskId) {
        return jpaRepository.findByTaskIdOrderByCreatedAtAsc(taskId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Attachment toDomain(AttachmentEntity e) {
        return new Attachment(
                e.getId(), e.getTaskId(), e.getUserId(),
                e.getFileName(), e.getStoredPath(), e.getContentType(),
                e.getSize(), e.getCreatedAt()
        );
    }

    private AttachmentEntity toEntity(Attachment a) {
        AttachmentEntity e = new AttachmentEntity();
        if (a.getId() != null) e.setId(a.getId());
        e.setTaskId(a.getTaskId());
        e.setUserId(a.getUserId());
        e.setFileName(a.getFileName());
        e.setStoredPath(a.getStoredPath());
        e.setContentType(a.getContentType());
        e.setSize(a.getSize());
        e.setCreatedAt(a.getCreatedAt());
        return e;
    }
}
