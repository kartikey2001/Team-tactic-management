package org.example.team_tactic.domain;

import java.time.Instant;

public final class Team {

    private final Long id;
    private final String name;
    private final String description;
    private final Long createdById;
    private final Instant createdAt;

    public Team(Long id, String name, String description, Long createdById, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdById = createdById;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Long getCreatedById() { return createdById; }
    public Instant getCreatedAt() { return createdAt; }

    public static Team create(String name, String description, Long createdById) {
        return new Team(null, name, description, createdById, null);
    }
}
