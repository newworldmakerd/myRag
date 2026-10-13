package io.lyh.ai.myrag.dto;

public record RetrievalResult(
        Long documentId,
        Long chunkId,
        Integer chunkIndex,
        String content,
        float score
) {
}
