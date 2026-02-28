package org.example.team_tactic.application.port;

/**
 * Port for JWT generation and validation. Implemented in infrastructure.
 */
public interface TokenProvider {

    String generateToken(Long userId, String email);

    Long getUserIdFromToken(String token);

    boolean validateToken(String token);
}
