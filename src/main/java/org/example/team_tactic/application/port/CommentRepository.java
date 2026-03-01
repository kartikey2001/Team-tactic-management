package org.example.team_tactic.application.port;

import org.example.team_tactic.domain.Comment;

import java.util.List;
import java.util.Optional;

/**
 * Port for comment persistence.
 */
public interface CommentRepository {

    Comment save(Comment comment);

    Optional<Comment> findById(Long id);

    List<Comment> findByTaskIdOrderByCreatedAtAsc(Long taskId);

    void deleteById(Long id);
}
