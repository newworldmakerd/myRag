package io.lyh.ai.myrag.dto;

import java.util.List;

public record RagAnswer(
        String answer,
        List<RetrievalResult> sources
) {
}
