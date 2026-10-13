package io.lyh.ai.myrag.retrieval;

import io.lyh.ai.myrag.dto.RetrievalQuery;
import io.lyh.ai.myrag.dto.RetrievalResult;
import io.lyh.ai.myrag.dto.VectorSearchResult;
import io.lyh.ai.myrag.embedding.EmbeddingService;
import io.lyh.ai.myrag.storage.VectorStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultRetriever implements  Retriever {

    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;

    @Override
    public List<RetrievalResult> retrieve(RetrievalQuery query) {
        float[] queryVector = embeddingService
                .embed(List.of(query.query()))
                .get(0);

        List<VectorSearchResult> results =
                vectorStore.search(
                        query.knowledgeBaseId(),
                        queryVector,
                        query.topK()
                );

        return results.stream()
                .map(item -> new RetrievalResult(
                        item.documentId(),
                        item.chunkId(),
                        item.chunkIndex(),
                        item.content(),
                        item.score()
                ))
                .toList();
    }
}
