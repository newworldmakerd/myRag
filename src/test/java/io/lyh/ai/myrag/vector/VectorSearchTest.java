package io.lyh.ai.myrag.vector;

import io.lyh.ai.myrag.dto.VectorSearchResult;
import io.lyh.ai.myrag.embedding.EmbeddingService;
import io.lyh.ai.myrag.storage.VectorStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class VectorSearchTest {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private EmbeddingService embeddingService;

    @Test
    void testSearch(){
        String question = "什么是检索增强生成（RAG）？它的核心原理和工作流程是什么？";

        float[] vector = embeddingService
                .embed(List.of(question))
                .get(0);

        List<VectorSearchResult> results =
                vectorStore.search(1L, vector, 5);

        results.forEach(result -> {
            System.out.println("score = " + result.score());
            System.out.println("documentId = " + result.documentId());
            System.out.println("content = " + result.content());
            System.out.println("----------------");
        });
    }
}
