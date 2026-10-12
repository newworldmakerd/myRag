package io.lyh.ai.myrag.chunker;

import java.util.List;

public interface TextChunker {
    List<String> split(String text);
}
