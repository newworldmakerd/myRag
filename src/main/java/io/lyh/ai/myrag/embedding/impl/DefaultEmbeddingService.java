package io.lyh.ai.myrag.embedding.impl;

import io.lyh.ai.myrag.embedding.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultEmbeddingService implements EmbeddingService {

    private final EmbeddingModel embeddingModel;

    @Override
    public List<float[]> embed(List<String> texts) {

        if (CollectionUtils.isEmpty(texts)) {
            return List.of();
        }

        if (texts.stream().anyMatch(
                text -> text == null || text.isBlank())) {
            throw new IllegalArgumentException(
                    "Embedding texts cannot be blank"
            );
        }

        List<float[]> vectors = embeddingModel.embed(texts);

        if (vectors.size() != texts.size()) {
            throw new IllegalStateException(
                    "Embedding result count mismatch"
            );
        }

        for (float[] vector : vectors) {
            if (vector == null || vector.length != 1024) {
                throw new IllegalStateException(
                        "Unexpected embedding dimension"
                );
            }
        }

        return vectors;
    }
}
