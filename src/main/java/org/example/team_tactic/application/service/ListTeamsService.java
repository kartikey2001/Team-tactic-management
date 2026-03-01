package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.TeamRepository;
import org.example.team_tactic.domain.Team;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListTeamsService {

    private final TeamRepository teamRepository;

    public ListTeamsService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<Team> listMyTeams(Long userId) {
        return teamRepository.findByMemberUserId(userId);
    }
}
