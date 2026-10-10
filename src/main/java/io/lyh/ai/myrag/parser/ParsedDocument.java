package io.lyh.ai.myrag.parser;

public record ParsedDocument(
        String content,
        String fileName,
        String mimeType
) {
}
