package io.lyh.ai.myrag.controller;

import io.lyh.ai.myrag.service.RagService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/ask")
    public RagService.RagAnswer getRagService(
            @RequestBody RagRequest ragRequest
            ) {
        if (ragRequest == null || ragRequest.question().isBlank()) {
            throw new IllegalArgumentException("question is required");
        }

        return ragService.ask(ragRequest.question());
    }

    public record RagRequest(String question){}
}
