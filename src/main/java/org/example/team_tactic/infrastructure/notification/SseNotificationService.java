package org.example.team_tactic.infrastructure.notification;

import org.example.team_tactic.application.port.NotificationService;
import org.example.team_tactic.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSE-based implementation of NotificationService. Stores one emitter per user (latest wins).
 */
@Service
public class SseNotificationService implements NotificationService {

    private static final long EMITTER_TIMEOUT_MS = 60_000;
    private static final Logger log = LoggerFactory.getLogger(SseNotificationService.class);

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public void registerEmitter(Long userId, SseEmitter emitter) {
        SseEmitter previous = emitters.put(userId, emitter);
        if (previous != null) {
            try {
                previous.completeWithError(new IllegalStateException("Replaced by new connection"));
            } catch (Exception ignored) {
            }
        }
        emitter.onCompletion(() -> emitters.remove(userId, emitter));
        emitter.onTimeout(() -> {
            emitters.remove(userId, emitter);
            emitter.complete();
        });
    }

    @Override
    public void publishTaskAssigned(Long assigneeId, Task task) {
        if (assigneeId == null) return;
        send(assigneeId, "TASK_ASSIGNED", task);
    }

    @Override
    public void publishTaskUpdated(Long assigneeId, Task task) {
        if (assigneeId == null) return;
        send(assigneeId, "TASK_UPDATED", task);
    }

    private void send(Long userId, String type, Task task) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;
        try {
            Map<String, Object> payload = Map.of("type", type, "task", task);
            emitter.send(SseEmitter.event().name("notification").data(payload));
        } catch (IOException e) {
            log.warn("Failed to send notification to user {}: {}", userId, e.getMessage());
            emitters.remove(userId, emitter);
            try {
                emitter.completeWithError(e);
            } catch (Exception ignored) {
            }
        }
    }
}
