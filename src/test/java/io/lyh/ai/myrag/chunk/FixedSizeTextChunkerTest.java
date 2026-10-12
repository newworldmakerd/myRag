package io.lyh.ai.myrag.chunk;

import io.lyh.ai.myrag.chunker.FixedSizeTextChunker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class FixedSizeTextChunkerTest {

    private final FixedSizeTextChunker chunker = new FixedSizeTextChunker();

    @Test
    void testShortText() {
        List<String> chunks = chunker.split("Hello myRag");

        assertEquals(1, chunks.size());
        assertEquals("Hello myRag", chunks.get(0));
    }

    @Test
    void testLongText() {

        String text = "A".repeat(2000);

        List<String> chunks = chunker.split(text);

        assertTrue(chunks.size() > 1);

        assertEquals(800, chunks.get(0).length());

        // 第二块开头应该与第一块结尾重叠
        assertEquals(
                chunks.get(0).substring(650),
                chunks.get(1).substring(0, 150)
        );
    }

    @Test
    void testEmptyText() {

        assertTrue(chunker.split("").isEmpty());
        assertTrue(chunker.split("   ").isEmpty());
        assertTrue(chunker.split(null).isEmpty());
    }

}
