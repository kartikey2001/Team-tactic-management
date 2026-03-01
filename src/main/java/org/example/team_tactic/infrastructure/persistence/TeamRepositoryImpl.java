package org.example.team_tactic.infrastructure.persistence;

import org.example.team_tactic.application.port.TeamRepository;
import org.example.team_tactic.domain.Team;
import org.example.team_tactic.infrastructure.persistence.entity.TeamEntity;
import org.example.team_tactic.infrastructure.persistence.repository.TeamJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TeamRepositoryImpl implements TeamRepository {

    private final TeamJpaRepository jpaRepository;

    public TeamRepositoryImpl(TeamJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Team save(Team team) {
        TeamEntity e = toEntity(team);
        e = jpaRepository.save(e);
        return toDomain(e);
    }

    @Override
    public Optional<Team> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Team> findByMemberUserId(Long userId) {
        return jpaRepository.findByMemberUserId(userId).stream().map(this::toDomain).toList();
    }

    private Team toDomain(TeamEntity e) {
        return new Team(e.getId(), e.getName(), e.getDescription(), e.getCreatedById(), e.getCreatedAt());
    }

    private TeamEntity toEntity(Team t) {
        TeamEntity e = new TeamEntity();
        if (t.getId() != null) e.setId(t.getId());
        e.setName(t.getName());
        e.setDescription(t.getDescription());
        e.setCreatedById(t.getCreatedById());
        e.setCreatedAt(t.getCreatedAt());
        return e;
    }
}
