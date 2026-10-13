package io.lyh.ai.myrag.storage;

import io.lyh.ai.myrag.dto.VectorSearchResult;
import io.lyh.ai.myrag.entity.VectorRecord;

import java.util.List;
import java.util.UUID;

public interface VectorStore {

    void upsert(List<VectorRecord> records);

    void delete(List<UUID> vectorIds);

    List<VectorSearchResult> search(
            Long knowledgeBaseId,
            float[] queryVector,
            int topK
    );
}
