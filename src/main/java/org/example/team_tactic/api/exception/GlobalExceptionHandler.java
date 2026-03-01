package org.example.team_tactic.api.exception;

import org.example.team_tactic.api.dto.ErrorResponse;
import org.example.team_tactic.application.service.AddTeamMemberService;
import org.example.team_tactic.application.service.DeleteCommentService;
import org.example.team_tactic.application.service.GetProfileService;
import org.example.team_tactic.application.service.GetTeamService;
import org.example.team_tactic.application.service.LoginService;
import org.example.team_tactic.application.service.RegisterUserService;
import org.example.team_tactic.application.service.RemoveTeamMemberService;
import org.example.team_tactic.application.service.UpdateTaskService;
import org.example.team_tactic.application.service.AssignTaskService;
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

    @ExceptionHandler(UpdateTaskService.TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(UpdateTaskService.TaskNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(ex.getMessage(), "TASK_NOT_FOUND"));
    }

    @ExceptionHandler(AssignTaskService.AssigneeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAssigneeNotFound(AssignTaskService.AssigneeNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(ex.getMessage(), "ASSIGNEE_NOT_FOUND"));
    }

    @ExceptionHandler(GetTeamService.TeamNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTeamNotFound(GetTeamService.TeamNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(ex.getMessage(), "TEAM_NOT_FOUND"));
    }

    @ExceptionHandler(AddTeamMemberService.NotTeamMemberException.class)
    public ResponseEntity<ErrorResponse> handleNotTeamMember(AddTeamMemberService.NotTeamMemberException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(ex.getMessage(), "NOT_TEAM_MEMBER"));
    }

    @ExceptionHandler(AddTeamMemberService.AlreadyMemberException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyMember(AddTeamMemberService.AlreadyMemberException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(ex.getMessage(), "ALREADY_MEMBER"));
    }

    @ExceptionHandler(AssignTaskService.AssigneeNotTeamMemberException.class)
    public ResponseEntity<ErrorResponse> handleAssigneeNotTeamMember(AssignTaskService.AssigneeNotTeamMemberException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(ex.getMessage(), "ASSIGNEE_NOT_TEAM_MEMBER"));
    }

    @ExceptionHandler(DeleteCommentService.CommentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFound(DeleteCommentService.CommentNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(ex.getMessage(), "COMMENT_NOT_FOUND"));
    }

    @ExceptionHandler(DeleteCommentService.ForbiddenToDeleteCommentException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenToDeleteComment(DeleteCommentService.ForbiddenToDeleteCommentException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(ex.getMessage(), "FORBIDDEN_TO_DELETE_COMMENT"));
    }

    @ExceptionHandler(RemoveTeamMemberService.MemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMemberNotFound(RemoveTeamMemberService.MemberNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(ex.getMessage(), "MEMBER_NOT_FOUND"));
    }

    @ExceptionHandler(RemoveTeamMemberService.OnlyOwnerCanRemoveException.class)
    public ResponseEntity<ErrorResponse> handleOnlyOwnerCanRemove(RemoveTeamMemberService.OnlyOwnerCanRemoveException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(ex.getMessage(), "ONLY_OWNER_CAN_REMOVE"));
    }

    @ExceptionHandler(RemoveTeamMemberService.CannotRemoveLastOwnerException.class)
    public ResponseEntity<ErrorResponse> handleCannotRemoveLastOwner(RemoveTeamMemberService.CannotRemoveLastOwnerException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(ex.getMessage(), "CANNOT_REMOVE_LAST_OWNER"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(ex.getMessage(), "BAD_REQUEST"));
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
