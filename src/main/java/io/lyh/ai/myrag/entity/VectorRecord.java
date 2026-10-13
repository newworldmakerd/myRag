package io.lyh.ai.myrag.entity;

import java.util.UUID;

public record VectorRecord(
        UUID vectorId,
        Long knowledgeBaseId,
        Long documentId,
        Long chunkId,
        Integer chunkIndex,
        String content,
        float[] vector
) {
}
