package org.example.team_tactic.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Set;

/**
 * Validates critical JWT configuration at startup.
 * Fails fast in production to prevent insecure deployments.
 */
@Component
public class JwtStartupValidation {

    private static final int MIN_SECRET_LENGTH = 32;
    private static final Set<String> BLOCKED_SECRETS = Set.of(
            "dev-secret-key-min-32-chars-for-hs256",
            "changeme",
            "secret",
            "password"
    );

    private final Environment environment;
    private final String jwtSecret;
    private final long expirationMs;

    public JwtStartupValidation(
            Environment environment,
            @Value("${app.jwt.secret}") String jwtSecret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.environment = environment;
        this.jwtSecret = jwtSecret;
        this.expirationMs = expirationMs;
    }

    @PostConstruct
    public void validate() {
        boolean isProd = Arrays.stream(environment.getActiveProfiles())
                .anyMatch("prod"::equalsIgnoreCase);

        if (!isProd) {
            return;
        }

        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("Invalid JWT configuration for prod: app.jwt.secret is missing");
        }

        String normalized = jwtSecret.trim();
        if (normalized.length() < MIN_SECRET_LENGTH) {
            throw new IllegalStateException("Invalid JWT configuration for prod: app.jwt.secret must be at least 32 characters");
        }

        if (BLOCKED_SECRETS.contains(normalized.toLowerCase())) {
            throw new IllegalStateException("Invalid JWT configuration for prod: app.jwt.secret uses a blocked insecure value");
        }

        if (expirationMs <= 0) {
            throw new IllegalStateException("Invalid JWT configuration: app.jwt.expiration-ms must be positive");
        }
    }
}
