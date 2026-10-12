package io.lyh.ai.myrag.chunker;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class FixedSizeTextChunker implements TextChunker {

    private static final int CHUNK_SIZE = 800;
    private static final int OVERLAP = 150;

    @Override
    public List<String> split(String text) {

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        List<String> chunks = new ArrayList<>();

        int start = 0;
        int length = text.length();

        while (start < length) {
            int end = Math.min(start + CHUNK_SIZE, length);

            String chunk = text.substring(start, end).trim();

            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            if (end == length) {
                break;
            }

            start = end - OVERLAP;
        }

        return chunks;
    }
}
