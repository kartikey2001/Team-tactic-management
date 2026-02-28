package org.example.team_tactic.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String error,
        String code,
        List<FieldError> fieldErrors
) {
    public static ErrorResponse of(String error, String code) {
        return new ErrorResponse(error, code, null);
    }

    public static ErrorResponse validation(String error, List<FieldError> fieldErrors) {
        return new ErrorResponse(error, "VALIDATION_FAILED", fieldErrors);
    }

    public record FieldError(String field, String message) {}
}
