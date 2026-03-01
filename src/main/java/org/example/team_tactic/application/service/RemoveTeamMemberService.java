package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.TeamMemberRepository;
import org.example.team_tactic.domain.TeamMember;
import org.example.team_tactic.domain.TeamRole;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Remove a member from a team, or leave the team (when userIdToRemove == requestingUserId).
 * Only OWNER can remove other members; any member can remove themselves.
 * Cannot remove the last OWNER.
 */
@Service
public class RemoveTeamMemberService {

    private final GetTeamService getTeamService;
    private final TeamMemberRepository teamMemberRepository;

    public RemoveTeamMemberService(GetTeamService getTeamService, TeamMemberRepository teamMemberRepository) {
        this.getTeamService = getTeamService;
        this.teamMemberRepository = teamMemberRepository;
    }

    public void remove(Long teamId, Long userIdToRemove, Long requestingUserId) {
        getTeamService.getById(teamId);
        if (!getTeamService.isMember(teamId, requestingUserId)) {
            throw new AddTeamMemberService.NotTeamMemberException(teamId, requestingUserId);
        }

        TeamMember toRemove = teamMemberRepository.findByTeamIdAndUserId(teamId, userIdToRemove)
                .orElseThrow(() -> new MemberNotFoundException(teamId, userIdToRemove));

        if (requestingUserId.equals(userIdToRemove)) {
            teamMemberRepository.deleteByTeamIdAndUserId(teamId, userIdToRemove);
            return;
        }

        TeamMember requester = teamMemberRepository.findByTeamIdAndUserId(teamId, requestingUserId).orElseThrow();
        if (requester.getRole() != TeamRole.OWNER) {
            throw new OnlyOwnerCanRemoveException(teamId);
        }
        if (toRemove.getRole() == TeamRole.OWNER) {
            List<TeamMember> owners = teamMemberRepository.findByTeamId(teamId).stream()
                    .filter(m -> m.getRole() == TeamRole.OWNER)
                    .toList();
            if (owners.size() <= 1) {
                throw new CannotRemoveLastOwnerException(teamId);
            }
        }
        teamMemberRepository.deleteByTeamIdAndUserId(teamId, userIdToRemove);
    }

    public static final class MemberNotFoundException extends RuntimeException {
        public MemberNotFoundException(Long teamId, Long userId) {
            super("User " + userId + " is not a member of team " + teamId);
        }
    }

    public static final class OnlyOwnerCanRemoveException extends RuntimeException {
        public OnlyOwnerCanRemoveException(Long teamId) {
            super("Only team owner can remove other members from team " + teamId);
        }
    }

    public static final class CannotRemoveLastOwnerException extends RuntimeException {
        public CannotRemoveLastOwnerException(Long teamId) {
            super("Cannot remove the last owner of team " + teamId);
        }
    }
}
