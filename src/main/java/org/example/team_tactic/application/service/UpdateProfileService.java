package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.UserRepository;
import org.example.team_tactic.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Updates the current user's profile (e.g. display name).
 */
@Service
public class UpdateProfileService {

    private final UserRepository userRepository;

    public UpdateProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User updateProfile(Long userId, String displayName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GetProfileService.UserNotFoundException(userId));
        User updated = user.withDisplayName(displayName);
        return userRepository.save(updated);
    }
}
