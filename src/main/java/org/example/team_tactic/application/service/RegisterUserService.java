package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.TokenProvider;
import org.example.team_tactic.application.port.UserRepository;
import org.example.team_tactic.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registers a new user. Fails if email already exists.
 */
@Service
public class RegisterUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String email, String rawPassword, String displayName) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
        String passwordHash = passwordEncoder.encode(rawPassword);
        User user = User.create(email, passwordHash, displayName);
        return userRepository.save(user);
    }

    public static final class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String email) {
            super("Email already registered: " + email);
        }
    }
}
