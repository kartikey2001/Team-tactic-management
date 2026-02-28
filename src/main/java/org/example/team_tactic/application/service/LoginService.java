package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.TokenProvider;
import org.example.team_tactic.application.port.UserRepository;
import org.example.team_tactic.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authenticates user and returns JWT.
 */
@Service
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public LoginService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public String login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException());
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return tokenProvider.generateToken(user.getId(), user.getEmail());
    }

    public static final class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() {
            super("Invalid email or password");
        }
    }
}
