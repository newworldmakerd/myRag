package io.lyh.ai.myrag.dto;

public record RagQuery(
        Long knowledgeBaseId,
        String question,
        int topK
) {
}
