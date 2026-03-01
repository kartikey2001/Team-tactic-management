package org.example.team_tactic.api.dto;

/** Pass assigneeId to assign, or null to unassign. */
public record AssignTaskRequest(Long assigneeId) {}
