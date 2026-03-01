package org.example.team_tactic.application.service;

import org.example.team_tactic.application.port.AttachmentRepository;
import org.example.team_tactic.domain.Attachment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListAttachmentsService {

    private final AttachmentRepository attachmentRepository;
    private final ListTasksService listTasksService;

    public ListAttachmentsService(AttachmentRepository attachmentRepository,
                                  ListTasksService listTasksService) {
        this.attachmentRepository = attachmentRepository;
        this.listTasksService = listTasksService;
    }

    public List<Attachment> listByTaskId(Long taskId, Long requestingUserId) {
        listTasksService.getByIdAndEnsureAccess(taskId, requestingUserId);
        return attachmentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);
    }
}
