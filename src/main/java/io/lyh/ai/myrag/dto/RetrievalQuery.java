package io.lyh.ai.myrag.dto;

public record RetrievalQuery(
        Long knowledgeBaseId,
        String query,
        int topK
) {
}
