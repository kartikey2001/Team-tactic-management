package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.TeamMemberRepository;
import org.example.team_tactic.application.port.TeamRepository;
import org.example.team_tactic.domain.Team;
import org.springframework.stereotype.Service;

@Service
public class GetTeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public GetTeamService(TeamRepository teamRepository, TeamMemberRepository teamMemberRepository) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    public Team getById(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException(teamId));
    }

    public boolean isMember(Long teamId, Long userId) {
        return teamMemberRepository.existsByTeamIdAndUserId(teamId, userId);
    }

    public static final class TeamNotFoundException extends RuntimeException {
        public TeamNotFoundException(Long teamId) {
            super("Team not found: " + teamId);
        }
    }
}
