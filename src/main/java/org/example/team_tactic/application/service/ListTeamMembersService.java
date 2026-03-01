package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.TeamMemberRepository;
import org.example.team_tactic.domain.TeamMember;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListTeamMembersService {

    private final TeamMemberRepository teamMemberRepository;
    private final GetTeamService getTeamService;

    public ListTeamMembersService(TeamMemberRepository teamMemberRepository, GetTeamService getTeamService) {
        this.teamMemberRepository = teamMemberRepository;
        this.getTeamService = getTeamService;
    }

    public List<TeamMember> listMembers(Long teamId, Long requestingUserId) {
        getTeamService.getById(teamId);
        if (!getTeamService.isMember(teamId, requestingUserId)) {
            throw new AddTeamMemberService.NotTeamMemberException(teamId, requestingUserId);
        }
        return teamMemberRepository.findByTeamId(teamId);
    }
}
