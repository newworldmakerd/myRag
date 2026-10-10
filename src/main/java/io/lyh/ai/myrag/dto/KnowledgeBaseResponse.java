package io.lyh.ai.myrag.dto;

import java.time.OffsetDateTime;

public record KnowledgeBaseResponse(
        Long id,
        String name,
        String description,
        Short status,
        OffsetDateTime createTime
) {
}
