package org.example.team_tactic.application.port;

import org.example.team_tactic.domain.Task;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Port for real-time notifications. Implementations manage SSE connections and push events.
 */
public interface NotificationService {

    /**
     * Registers an SSE emitter for the given user. The emitter will receive task notifications.
     */
    void registerEmitter(Long userId, SseEmitter emitter);

    /**
     * Notifies the assignee that a task was assigned to them.
     */
    void publishTaskAssigned(Long assigneeId, Task task);

    /**
     * Notifies the assignee that a task they are assigned to was updated.
     */
    void publishTaskUpdated(Long assigneeId, Task task);
}
