package org.example.team_tactic.api.controller;

import jakarta.validation.Valid;
import org.example.team_tactic.api.dto.UpdateProfileRequest;
import org.example.team_tactic.api.dto.UserProfileResponse;
import org.example.team_tactic.application.service.GetProfileService;
import org.example.team_tactic.application.service.UpdateProfileService;
import org.example.team_tactic.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final GetProfileService getProfileService;
    private final UpdateProfileService updateProfileService;

    public UserController(GetProfileService getProfileService, UpdateProfileService updateProfileService) {
        this.getProfileService = getProfileService;
        this.updateProfileService = updateProfileService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMe(@CurrentUserId Long userId) {
        User user = getProfileService.getProfile(userId);
        return ResponseEntity.ok(toProfileResponse(user));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMe(@CurrentUserId Long userId,
                                                        @Valid @RequestBody UpdateProfileRequest request) {
        User user = updateProfileService.updateProfile(userId, request.displayName());
        return ResponseEntity.ok(toProfileResponse(user));
    }

    private static UserProfileResponse toProfileResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getCreatedAt()
        );
    }
}
