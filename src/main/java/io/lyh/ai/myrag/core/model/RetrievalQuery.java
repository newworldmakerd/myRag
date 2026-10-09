package io.lyh.ai.myrag.core.model;

public record RetrievalQuery(
        String query,
        int topK
) {
}
