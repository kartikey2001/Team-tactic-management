package org.example.team_tactic.application.port;

import org.example.team_tactic.domain.Attachment;

import java.util.List;
import java.util.Optional;

/**
 * Port for attachment persistence.
 */
public interface AttachmentRepository {

    Attachment save(Attachment attachment);

    Optional<Attachment> findById(Long id);

    List<Attachment> findByTaskIdOrderByCreatedAtAsc(Long taskId);

    void deleteById(Long id);
}
