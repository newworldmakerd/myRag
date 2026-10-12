package io.lyh.ai.myrag.config;

import io.lyh.ai.myrag.chunker.RecursiveTextChunker;
import io.lyh.ai.myrag.chunker.TextChunker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ChunkerConfig {

    @Bean
    @Primary
    public TextChunker recursiveTextChunker() {
        return new RecursiveTextChunker(800, 150);
    }
}
