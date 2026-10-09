package io.lyh.ai.myrag.retrieval.dense;

import io.lyh.ai.myrag.core.model.RetrievalQuery;
import io.lyh.ai.myrag.core.model.RetrievedChunk;
import io.lyh.ai.myrag.core.spi.Retriever;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class QdrantDenseRetriever implements Retriever {

    private final VectorStore vectorStore;

    public QdrantDenseRetriever(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public List<RetrievedChunk> retrieve(RetrievalQuery query) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query.query())
                .topK(query.topK())
                .similarityThreshold(query.similarityThreshold())
                .build();

        List<Document> documents = vectorStore.similaritySearch(searchRequest);

        if (CollectionUtils.isEmpty(documents)) {
            return List.of();
        }

        return documents.stream()
                .map(doc -> new RetrievedChunk(
                        doc.getId(),
                        doc.getText(),
                        doc.getScore() == null ? 0.0 : doc.getScore(),
                        "qdrant-dense",
                        doc.getMetadata()
                ))
                .toList();
    }
}
