package io.lyh.ai.myrag.controller;

import io.lyh.ai.myrag.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/knowledge-bases/{knowledgeBaseId}/documents")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public Object upload(
            @PathVariable Long knowledgeBaseId,
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        return documentService.upload(knowledgeBaseId, file);
    }

}
