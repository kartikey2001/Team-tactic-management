package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.CommentRepository;
import org.example.team_tactic.application.port.TaskRepository;
import org.example.team_tactic.application.port.TeamMemberRepository;
import org.example.team_tactic.domain.Comment;
import org.example.team_tactic.domain.Task;
import org.example.team_tactic.domain.TaskStatus;
import org.example.team_tactic.domain.TeamMember;
import org.example.team_tactic.domain.TeamRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteCommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ListTasksService listTasksService;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private DeleteCommentService deleteCommentService;

    @Test
    void delete_authorCanDelete() {
        Comment comment = comment(50L, 10L, 1L);
        when(commentRepository.findById(50L)).thenReturn(Optional.of(comment));
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task(10L, 100L)));

        deleteCommentService.delete(50L, 10L, 1L);

        verify(listTasksService).getByIdAndEnsureAccess(10L, 1L);
        verify(commentRepository).deleteById(50L);
    }

    @Test
    void delete_teamOwnerCanDeleteOthersComment() {
        Comment comment = comment(50L, 10L, 2L);
        when(commentRepository.findById(50L)).thenReturn(Optional.of(comment));
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task(10L, 100L)));
        when(teamMemberRepository.findByTeamIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(new TeamMember(1L, 100L, 1L, TeamRole.OWNER, Instant.now())));

        deleteCommentService.delete(50L, 10L, 1L);

        verify(listTasksService).getByIdAndEnsureAccess(10L, 1L);
        verify(commentRepository).deleteById(50L);
    }

    @Test
    void delete_nonOwnerCannotDeleteOthersComment() {
        Comment comment = comment(50L, 10L, 2L);
        when(commentRepository.findById(50L)).thenReturn(Optional.of(comment));
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task(10L, 100L)));
        when(teamMemberRepository.findByTeamIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(new TeamMember(1L, 100L, 1L, TeamRole.MEMBER, Instant.now())));

        assertThrows(DeleteCommentService.ForbiddenToDeleteCommentException.class,
                () -> deleteCommentService.delete(50L, 10L, 1L));

        verify(commentRepository, never()).deleteById(50L);
    }

    @Test
    void delete_commentNotFound_throwsCommentNotFound() {
        when(commentRepository.findById(50L)).thenReturn(Optional.empty());

        assertThrows(DeleteCommentService.CommentNotFoundException.class,
                () -> deleteCommentService.delete(50L, 10L, 1L));

        verify(listTasksService, never()).getByIdAndEnsureAccess(10L, 1L);
    }

    @Test
    void delete_taskIdMismatch_throwsCommentNotFound() {
        Comment comment = comment(50L, 999L, 1L);
        when(commentRepository.findById(50L)).thenReturn(Optional.of(comment));

        assertThrows(DeleteCommentService.CommentNotFoundException.class,
                () -> deleteCommentService.delete(50L, 10L, 1L));

        verify(commentRepository, never()).deleteById(50L);
    }

    private static Comment comment(Long id, Long taskId, Long userId) {
        return new Comment(id, taskId, userId, "hello", Instant.now());
    }

    private static Task task(Long taskId, Long teamId) {
        Instant now = Instant.now();
        return new Task(taskId, "T", "D", TaskStatus.OPEN, now.plusSeconds(3600),
                teamId, 2L, 1L, now.minusSeconds(100), now.minusSeconds(50));
    }
}
