package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.TeamMemberRepository;
import org.example.team_tactic.application.port.TeamRepository;
import org.example.team_tactic.domain.Team;
import org.example.team_tactic.domain.TeamMember;
import org.example.team_tactic.domain.TeamRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class CreateTeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public CreateTeamService(TeamRepository teamRepository, TeamMemberRepository teamMemberRepository) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    @Transactional
    public Team create(String name, String description, Long createdById) {
        Team team = Team.create(name, description, createdById);
        team = teamRepository.save(team);
        TeamMember member = new TeamMember(null, team.getId(), createdById, TeamRole.OWNER, Instant.now());
        teamMemberRepository.save(member);
        return team;
    }
}
