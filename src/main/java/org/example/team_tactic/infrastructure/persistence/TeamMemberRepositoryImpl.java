package org.example.team_tactic.infrastructure.persistence;

import org.example.team_tactic.application.port.TeamMemberRepository;
import org.example.team_tactic.domain.TeamMember;
import org.example.team_tactic.domain.TeamRole;
import org.example.team_tactic.infrastructure.persistence.entity.TeamMemberEntity;
import org.example.team_tactic.infrastructure.persistence.repository.TeamMemberJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TeamMemberRepositoryImpl implements TeamMemberRepository {

    private final TeamMemberJpaRepository jpaRepository;

    public TeamMemberRepositoryImpl(TeamMemberJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TeamMember save(TeamMember member) {
        TeamMemberEntity e = toEntity(member);
        e = jpaRepository.save(e);
        return toDomain(e);
    }

    @Override
    public Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId) {
        return jpaRepository.findByTeamIdAndUserId(teamId, userId).map(this::toDomain);
    }

    @Override
    public List<TeamMember> findByTeamId(Long teamId) {
        return jpaRepository.findByTeamId(teamId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByTeamIdAndUserId(Long teamId, Long userId) {
        return jpaRepository.existsByTeamIdAndUserId(teamId, userId);
    }

    @Override
    public void deleteByTeamIdAndUserId(Long teamId, Long userId) {
        jpaRepository.deleteByTeamIdAndUserId(teamId, userId);
    }

    private TeamMember toDomain(TeamMemberEntity e) {
        return new TeamMember(e.getId(), e.getTeamId(), e.getUserId(), e.getRole(), e.getJoinedAt());
    }

    private TeamMemberEntity toEntity(TeamMember m) {
        TeamMemberEntity e = new TeamMemberEntity();
        if (m.getId() != null) e.setId(m.getId());
        e.setTeamId(m.getTeamId());
        e.setUserId(m.getUserId());
        e.setRole(m.getRole());
        e.setJoinedAt(m.getJoinedAt());
        return e;
    }
}
