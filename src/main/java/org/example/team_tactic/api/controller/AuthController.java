package org.example.team_tactic.api.controller;

import jakarta.validation.Valid;
import org.example.team_tactic.api.dto.AuthResponse;
import org.example.team_tactic.api.dto.LoginRequest;
import org.example.team_tactic.api.dto.RegisterRequest;
import org.example.team_tactic.application.port.TokenProvider;
import org.example.team_tactic.application.service.GetProfileService;
import org.example.team_tactic.application.service.LoginService;
import org.example.team_tactic.application.service.RegisterUserService;
import org.example.team_tactic.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserService registerUserService;
    private final LoginService loginService;
    private final GetProfileService getProfileService;
    private final TokenProvider tokenProvider;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    public AuthController(RegisterUserService registerUserService,
                          LoginService loginService,
                          GetProfileService getProfileService,
                          TokenProvider tokenProvider) {
        this.registerUserService = registerUserService;
        this.loginService = loginService;
        this.getProfileService = getProfileService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = registerUserService.register(
                request.email(),
                request.password(),
                request.displayName()
        );
        String token = tokenProvider.generateToken(user.getId(), user.getEmail());
        AuthResponse response = toAuthResponse(user, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = loginService.login(request.email(), request.password());
        Long userId = tokenProvider.getUserIdFromToken(token);
        User user = getProfileService.getProfile(userId);
        AuthResponse response = toAuthResponse(user, token);
        return ResponseEntity.ok(response);
    }

    private AuthResponse toAuthResponse(User user, String token) {
        Instant expiresAt = Instant.now().plusMillis(expirationMs);
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getDisplayName(),
                user.getId(),
                expiresAt
        );
    }
}
