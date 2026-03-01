package org.example.team_tactic.infrastructure.persistence.repository;

import org.example.team_tactic.infrastructure.persistence.entity.TeamMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberJpaRepository extends JpaRepository<TeamMemberEntity, Long> {

    Optional<TeamMemberEntity> findByTeamIdAndUserId(Long teamId, Long userId);

    List<TeamMemberEntity> findByTeamId(Long teamId);

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);

    void deleteByTeamIdAndUserId(Long teamId, Long userId);
}
