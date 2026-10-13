package io.lyh.ai.myrag.filter;

import io.qdrant.client.grpc.Common.Filter;
import static io.qdrant.client.ConditionFactory.match;

public class QdrantFilters {

    public static Filter knowledgeBase(Long knowledgeBaseId) {
        return Filter.newBuilder()
                .addMust(match("knowledge_base_id", knowledgeBaseId))
                .build();
    }

    public static Filter document(Long documentId) {
        return Filter.newBuilder()
                .addMust(match("document_id", documentId))
                .build();
    }
}
