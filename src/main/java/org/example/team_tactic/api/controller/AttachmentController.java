package org.example.team_tactic.api.controller;

import org.example.team_tactic.application.service.DeleteAttachmentService;
import org.example.team_tactic.application.service.GetAttachmentService;
import org.example.team_tactic.domain.Attachment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/attachments")
public class AttachmentController {

    private final GetAttachmentService getAttachmentService;
    private final DeleteAttachmentService deleteAttachmentService;

    public AttachmentController(GetAttachmentService getAttachmentService,
                               DeleteAttachmentService deleteAttachmentService) {
        this.getAttachmentService = getAttachmentService;
        this.deleteAttachmentService = deleteAttachmentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@CurrentUserId Long userId, @PathVariable Long id) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Attachment attachment = getAttachmentService.getByIdAndWriteContent(id, userId, output);
        byte[] content = output.toByteArray();
        String contentType = attachment.getContentType() != null
                ? attachment.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String encodedFileName = URLEncoder.encode(attachment.getFileName(), StandardCharsets.UTF_8)
                .replace("+", "%20");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(content.length)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFileName() + "\"; filename*=UTF-8''" + encodedFileName)
                .body(content);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@CurrentUserId Long userId, @PathVariable Long id) {
        deleteAttachmentService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
