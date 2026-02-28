package org.example.team_tactic.domain;

import java.time.Instant;

/**
 * Domain user. Password hash is stored but never exposed outside persistence.
 */
public final class User {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final String displayName;
    private final Instant createdAt;

    public User(Long id, String email, String passwordHash, String displayName, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /** New user (no id yet) for registration. */
    public static User create(String email, String passwordHash, String displayName) {
        return new User(null, email, passwordHash, displayName, Instant.now());
    }

    /** With id and createdAt (after persist). */
    public User withIdAndCreatedAt(Long id, Instant createdAt) {
        return new User(id, email, passwordHash, displayName, createdAt);
    }

    /** Update display name only (immutable). */
    public User withDisplayName(String newDisplayName) {
        return new User(id, email, passwordHash, newDisplayName, createdAt);
    }
}
