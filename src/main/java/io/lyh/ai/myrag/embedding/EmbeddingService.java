package io.lyh.ai.myrag.embedding;

import java.util.List;

public interface EmbeddingService {
    List<float[]> embed(List<String> texts);
}
