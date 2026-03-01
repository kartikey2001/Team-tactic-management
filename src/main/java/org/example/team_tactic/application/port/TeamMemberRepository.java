package org.example.team_tactic.application.port;

import org.example.team_tactic.domain.TeamMember;
import org.example.team_tactic.domain.TeamRole;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository {

    TeamMember save(TeamMember member);

    Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId);

    List<TeamMember> findByTeamId(Long teamId);

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);

    void deleteByTeamIdAndUserId(Long teamId, Long userId);
}
