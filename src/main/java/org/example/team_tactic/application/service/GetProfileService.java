package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.UserRepository;
import org.example.team_tactic.domain.User;
import org.springframework.stereotype.Service;

/**
 * Returns the current user's profile (no password).
 */
@Service
public class GetProfileService {

    private final UserRepository userRepository;

    public GetProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public static final class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(Long userId) {
            super("User not found: " + userId);
        }
    }
}
