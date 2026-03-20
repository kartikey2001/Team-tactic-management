package org.example.team_tactic.api.controller;

import org.example.team_tactic.api.dto.AttachmentResponse;
import org.example.team_tactic.application.service.CreateAttachmentService;
import org.example.team_tactic.application.service.ListAttachmentsService;
import org.example.team_tactic.domain.Attachment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/attachments")
public class TaskAttachmentController {

    private final CreateAttachmentService createAttachmentService;
    private final ListAttachmentsService listAttachmentsService;

    public TaskAttachmentController(CreateAttachmentService createAttachmentService,
                                    ListAttachmentsService listAttachmentsService) {
        this.createAttachmentService = createAttachmentService;
        this.listAttachmentsService = listAttachmentsService;
    }

    @PostMapping
    public ResponseEntity<AttachmentResponse> uploadAttachment(@CurrentUserId Long userId,
                                                               @PathVariable Long taskId,
                                                               @RequestParam("file") MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            Attachment attachment = createAttachmentService.create(
                    taskId, file.getOriginalFilename(), file.getContentType(), file.getSize(),
                    inputStream, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(toAttachmentResponse(attachment));
        }
    }

    @GetMapping
    public ResponseEntity<List<AttachmentResponse>> listAttachments(@CurrentUserId Long userId,
                                                                    @PathVariable Long taskId) {
        return ResponseEntity.ok(
                listAttachmentsService.listByTaskId(taskId, userId).stream()
                        .map(TaskAttachmentController::toAttachmentResponse)
                        .toList());
    }

    private static AttachmentResponse toAttachmentResponse(Attachment a) {
        return new AttachmentResponse(
                a.getId(), a.getTaskId(), a.getUserId(), a.getFileName(),
                a.getContentType(), a.getSize(), a.getCreatedAt());
    }
}
