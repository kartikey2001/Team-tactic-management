package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.TeamMemberRepository;
import org.example.team_tactic.application.port.UserRepository;
import org.example.team_tactic.domain.TeamMember;
import org.example.team_tactic.domain.TeamRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AddTeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final GetTeamService getTeamService;
    private final UserRepository userRepository;

    public AddTeamMemberService(TeamMemberRepository teamMemberRepository, GetTeamService getTeamService, UserRepository userRepository) {
        this.teamMemberRepository = teamMemberRepository;
        this.getTeamService = getTeamService;
        this.userRepository = userRepository;
    }

    @Transactional
    public TeamMember addMember(Long teamId, Long userIdToAdd, Long addedByUserId) {
        getTeamService.getById(teamId);
        if (!getTeamService.isMember(teamId, addedByUserId)) {
            throw new NotTeamMemberException(teamId, addedByUserId);
        }
        if (teamMemberRepository.existsByTeamIdAndUserId(teamId, userIdToAdd)) {
            throw new AlreadyMemberException(teamId, userIdToAdd);
        }
        if (userRepository.findById(userIdToAdd).isEmpty()) {
            throw new org.example.team_tactic.application.service.AssignTaskService.AssigneeNotFoundException(userIdToAdd);
        }
        TeamMember member = new TeamMember(null, teamId, userIdToAdd, TeamRole.MEMBER, Instant.now());
        return teamMemberRepository.save(member);
    }

    public static final class NotTeamMemberException extends RuntimeException {
        public NotTeamMemberException(Long teamId, Long userId) {
            super("User " + userId + " is not a member of team " + teamId);
        }
    }

    public static final class AlreadyMemberException extends RuntimeException {
        public AlreadyMemberException(Long teamId, Long userId) {
            super("User " + userId + " is already a member of team " + teamId);
        }
    }
}
