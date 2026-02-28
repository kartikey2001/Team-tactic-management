package org.example.team_tactic.api.exception;

import org.example.team_tactic.api.dto.ErrorResponse;
import org.example.team_tactic.application.service.GetProfileService;
import org.example.team_tactic.application.service.LoginService;
import org.example.team_tactic.application.service.RegisterUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegisterUserService.EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(RegisterUserService.EmailAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(ex.getMessage(), "EMAIL_ALREADY_EXISTS"));
    }

    @ExceptionHandler(LoginService.InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(LoginService.InvalidCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of("Invalid email or password", "INVALID_CREDENTIALS"));
    }

    @ExceptionHandler(GetProfileService.UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(GetProfileService.UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(ex.getMessage(), "USER_NOT_FOUND"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorResponse.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.validation("Validation failed", errors));
    }
}
