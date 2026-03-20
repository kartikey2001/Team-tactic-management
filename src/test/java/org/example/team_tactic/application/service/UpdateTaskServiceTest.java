package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.NotificationService;
import org.example.team_tactic.application.port.TaskRepository;
import org.example.team_tactic.domain.Task;
import org.example.team_tactic.domain.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateTaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private GetTeamService getTeamService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UpdateTaskService updateTaskService;

    @Test
    void update_partialFields_updatesAndPublishesWhenAssigneeExists() {
        Instant oldTime = Instant.now().minusSeconds(600);
        Instant newDueDate = Instant.now().plusSeconds(7200);
        Task existing = new Task(10L, "Old", "Old Desc", TaskStatus.OPEN, oldTime.plusSeconds(1000),
                100L, 2L, 1L, oldTime.minusSeconds(1000), oldTime);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(getTeamService.isMember(100L, 1L)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task updated = updateTaskService.update(10L, "New Title", null, TaskStatus.IN_PROGRESS, newDueDate, 1L);

        assertEquals("New Title", updated.getTitle());
        assertEquals("Old Desc", updated.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updated.getStatus());
        assertEquals(newDueDate, updated.getDueDate());
        verify(notificationService).publishTaskUpdated(2L, updated);
    }

    @Test
    void update_requesterNotTeamMember_throwsNotTeamMember() {
        Task existing = task(10L, 100L, 2L);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(getTeamService.isMember(100L, 99L)).thenReturn(false);

        assertThrows(AddTeamMemberService.NotTeamMemberException.class,
                () -> updateTaskService.update(10L, "X", "Y", TaskStatus.COMPLETED, Instant.now(), 99L));

        verify(taskRepository, never()).save(any(Task.class));
        verify(notificationService, never()).publishTaskUpdated(any(), any());
    }

    @Test
    void update_taskNotFound_throwsTaskNotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UpdateTaskService.TaskNotFoundException.class,
                () -> updateTaskService.update(999L, "X", null, null, null, 1L));

        verify(taskRepository, never()).save(any(Task.class));
        verify(notificationService, never()).publishTaskUpdated(any(), any());
    }

    @Test
    void update_noAssignee_doesNotPublishNotification() {
        Task existing = task(10L, 100L, null);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(getTeamService.isMember(100L, 1L)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task updated = updateTaskService.update(10L, null, "Changed", null, null, 1L);

        assertEquals("Changed", updated.getDescription());
        verify(notificationService, never()).publishTaskUpdated(any(), any());
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
                now.minusSeconds(100),
                now.minusSeconds(50)
        );
    }
}
