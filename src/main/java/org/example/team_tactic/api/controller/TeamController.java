package org.example.team_tactic.api.controller;

import jakarta.validation.Valid;
import org.example.team_tactic.api.dto.AddTeamMemberRequest;
import org.example.team_tactic.api.dto.CreateTeamRequest;
import org.example.team_tactic.api.dto.TeamMemberResponse;
import org.example.team_tactic.api.dto.TeamResponse;
import org.example.team_tactic.application.service.AddTeamMemberService;
import org.example.team_tactic.application.service.CreateTeamService;
import org.example.team_tactic.application.service.GetTeamService;
import org.example.team_tactic.application.service.ListTeamMembersService;
import org.example.team_tactic.application.service.ListTeamsService;
import org.example.team_tactic.domain.Team;
import org.example.team_tactic.domain.TeamMember;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final CreateTeamService createTeamService;
    private final ListTeamsService listTeamsService;
    private final GetTeamService getTeamService;
    private final AddTeamMemberService addTeamMemberService;
    private final ListTeamMembersService listTeamMembersService;

    public TeamController(CreateTeamService createTeamService, ListTeamsService listTeamsService,
                          GetTeamService getTeamService, AddTeamMemberService addTeamMemberService,
                          ListTeamMembersService listTeamMembersService) {
        this.createTeamService = createTeamService;
        this.listTeamsService = listTeamsService;
        this.getTeamService = getTeamService;
        this.addTeamMemberService = addTeamMemberService;
        this.listTeamMembersService = listTeamMembersService;
    }

    @PostMapping
    public ResponseEntity<TeamResponse> create(@CurrentUserId Long userId, @Valid @RequestBody CreateTeamRequest request) {
        Team team = createTeamService.create(request.name(), request.description(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toTeamResponse(team));
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> listMyTeams(@CurrentUserId Long userId) {
        List<Team> teams = listTeamsService.listMyTeams(userId);
        return ResponseEntity.ok(teams.stream().map(TeamController::toTeamResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getById(@CurrentUserId Long userId, @PathVariable Long id) {
        Team team = getTeamService.getById(id);
        if (!getTeamService.isMember(id, userId)) {
            throw new AddTeamMemberService.NotTeamMemberException(id, userId);
        }
        return ResponseEntity.ok(toTeamResponse(team));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<TeamMemberResponse>> listMembers(@CurrentUserId Long userId, @PathVariable Long id) {
        List<TeamMember> members = listTeamMembersService.listMembers(id, userId);
        return ResponseEntity.ok(members.stream().map(TeamController::toMemberResponse).toList());
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<TeamMemberResponse> addMember(@CurrentUserId Long userId, @PathVariable Long id,
                                                        @Valid @RequestBody AddTeamMemberRequest request) {
        TeamMember member = addTeamMemberService.addMember(id, request.userId(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toMemberResponse(member));
    }

    private static TeamResponse toTeamResponse(Team team) {
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getDescription(),
                team.getCreatedById(),
                team.getCreatedAt()
        );
    }

    private static TeamMemberResponse toMemberResponse(TeamMember m) {
        return new TeamMemberResponse(m.getId(), m.getTeamId(), m.getUserId(), m.getRole(), m.getJoinedAt());
    }
}
