package io.lyh.ai.myrag.chunk;

import io.lyh.ai.myrag.chunker.RecursiveTextChunker;
import io.lyh.ai.myrag.chunker.TextChunker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RecursiveTextChunkerTest {

    @Test
    void shortTextShouldRemainOneChunk(){
        TextChunker chunker = new RecursiveTextChunker(100,20);

        List<String> chunks = chunker.split(
                "Redis 是一个内存数据库。"
        );
        assertEquals(1, chunks.size());
    }

    @Test
    void longTextShouldBeSplit() {

        TextChunker chunker = new RecursiveTextChunker(100, 20);

        String text = """
                Redis 是一个高性能的内存数据库。
                Redis 支持多种数据结构。

                MySQL 是一个关系型数据库。
                MySQL 支持事务和 MVCC。
                """.repeat(10);

        List<String> chunks = chunker.split(text);

        assertTrue(chunks.size() > 1);

        for (String chunk : chunks) {
            assertTrue(chunk.length() <= 100);
        }
    }
}
