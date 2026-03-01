package org.example.team_tactic.infrastructure.persistence.repository;

import org.example.team_tactic.infrastructure.persistence.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamJpaRepository extends JpaRepository<TeamEntity, Long> {

    @Query("SELECT t FROM TeamEntity t, TeamMemberEntity m WHERE m.teamId = t.id AND m.userId = :userId")
    List<TeamEntity> findByMemberUserId(Long userId);
}
