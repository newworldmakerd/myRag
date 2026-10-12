package io.lyh.ai.myrag.embedding;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class EmbeddingServiceTest {

    @Autowired
    private EmbeddingService embeddingService;

    @Test
    void testEmbedding() {

        List<String> texts = List.of(
                "Java 是一种面向对象编程语言",
                "Qdrant 支持向量相似度检索",
                "RAG 可以将知识库内容提供给大语言模型"
        );
        List<float[]> vectors = embeddingService.embed(texts);

        assertEquals(3, vectors.size());

        for (float[] vector : vectors) {
            assertEquals(1024, vector.length);
        }
    }
}
