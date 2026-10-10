package io.lyh.ai.myrag.dto;

public record DocumentUploadResponse(
        Long documentId,
        Long knowledgeBaseId,
        String fileName,
        String status
) {
}
