package org.example.team_tactic.application.port;

import org.example.team_tactic.domain.User;

import java.util.Optional;

/**
 * Port for user persistence. Implemented in infrastructure.
 */
public interface UserRepository {

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    User save(User user);

    boolean existsByEmail(String email);
}
