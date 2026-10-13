package io.lyh.ai.myrag.vector;

import io.lyh.ai.myrag.embedding.EmbeddingService;
import io.lyh.ai.myrag.entity.VectorRecord;
import io.lyh.ai.myrag.storage.VectorStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest
public class QdrantVectorStoreTest {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private EmbeddingService embeddingService;

    @Test
    void testUpsert(){
        String content = "Spring Boot 是 Java 后端开发框架";

        float[] vector = embeddingService
                .embed(List.of(content))
                .get(0);

        UUID vectorId = UUID.randomUUID();

        VectorRecord record = new VectorRecord(
                vectorId,
                1L,
                1L,
                1L,
                0,
                content,
                vector
        );

        vectorStore.upsert(List.of(record));
    }
}
