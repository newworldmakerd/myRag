package io.lyh.ai.myrag.dto;

import java.util.UUID;

public record VectorSearchResult(
        UUID vectorId,
        Long documentId,
        Long chunkId,
        Integer chunkIndex,
        String content,
        float score
) {
}
