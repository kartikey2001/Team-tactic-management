package org.example.team_tactic.domain;

import java.time.Instant;

public final class TeamMember {

    private final Long id;
    private final Long teamId;
    private final Long userId;
    private final TeamRole role;
    private final Instant joinedAt;

    public TeamMember(Long id, Long teamId, Long userId, TeamRole role, Instant joinedAt) {
        this.id = id;
        this.teamId = teamId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public Long getId() { return id; }
    public Long getTeamId() { return teamId; }
    public Long getUserId() { return userId; }
    public TeamRole getRole() { return role; }
    public Instant getJoinedAt() { return joinedAt; }
}
