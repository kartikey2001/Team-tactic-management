package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.NotificationService;
import org.example.team_tactic.application.port.TaskRepository;
import org.example.team_tactic.application.port.UserRepository;
import org.example.team_tactic.domain.Task;
import org.example.team_tactic.domain.TaskStatus;
import org.example.team_tactic.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignTaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GetTeamService getTeamService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AssignTaskService assignTaskService;

    @Test
    void assign_validTeamMember_savesAndPublishesNotification() {
        Task existing = task(10L, 100L, null);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(getTeamService.isMember(100L, 1L)).thenReturn(true); // assigner is member
        when(getTeamService.isMember(100L, 2L)).thenReturn(true); // assignee is member
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L)));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = assignTaskService.assign(10L, 2L, 1L);

        assertEquals(2L, result.getAssigneeId());
        verify(taskRepository).save(any(Task.class));
        verify(notificationService).publishTaskAssigned(2L, result);
    }

    @Test
    void assign_assigneeNotFound_throwsAndDoesNotSave() {
        Task existing = task(10L, 100L, null);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(getTeamService.isMember(100L, 1L)).thenReturn(true);
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(AssignTaskService.AssigneeNotFoundException.class,
                () -> assignTaskService.assign(10L, 2L, 1L));

        verify(taskRepository, never()).save(any(Task.class));
        verify(notificationService, never()).publishTaskAssigned(any(), any());
    }

    @Test
    void assign_assignerNotTeamMember_throwsNotTeamMember() {
        Task existing = task(10L, 100L, null);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(getTeamService.isMember(100L, 1L)).thenReturn(false);

        assertThrows(AddTeamMemberService.NotTeamMemberException.class,
                () -> assignTaskService.assign(10L, 2L, 1L));

        verify(taskRepository, never()).save(any(Task.class));
        verify(notificationService, never()).publishTaskAssigned(any(), any());
    }

    @Test
    void assign_unassignWithNullAssignee_savesWithoutNotification() {
        Task existing = task(10L, 100L, 2L);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(getTeamService.isMember(100L, 1L)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = assignTaskService.assign(10L, null, 1L);

        assertNull(result.getAssigneeId());
        verify(taskRepository).save(any(Task.class));
        verify(notificationService, never()).publishTaskAssigned(any(), any());
    }

    private static Task task(Long id, Long teamId, Long assigneeId) {
        Instant now = Instant.now();
        return new Task(
                id,
                "Task",
                "Description",
                TaskStatus.OPEN,
                now.plusSeconds(3600),
                teamId,
                assigneeId,
                1L,
                now,
                now
        );
    }

    private static User user(Long id) {
        return new User(id, "u@example.com", "hash", "User", Instant.now());
    }
}
