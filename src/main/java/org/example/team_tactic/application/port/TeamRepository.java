package org.example.team_tactic.application.port;

import org.example.team_tactic.domain.Team;

import java.util.List;
import java.util.Optional;

public interface TeamRepository {

    Team save(Team team);

    Optional<Team> findById(Long id);

    boolean existsById(Long id);

    /** Teams where the user is a member (via team_members). */
    List<Team> findByMemberUserId(Long userId);
}
