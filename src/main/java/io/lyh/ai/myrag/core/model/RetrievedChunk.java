package io.lyh.ai.myrag.core.model;

import java.util.Map;

public record RetrievedChunk (
        String chunkId,
        String content,
        double score,
        String retrieverName,
        Map<String,Object> metadata
){
}
