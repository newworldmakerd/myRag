package io.lyh.ai.myrag.controller;

import io.lyh.ai.myrag.service.RagTestService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/rag")
public class RagController {

    private final RagTestService ragTestService;

    public RagController(RagTestService ragTestService) {
        this.ragTestService = ragTestService;
    }

    @PostMapping("/ask")
    public RagTestService.RagAnswer getRagService(
            @RequestBody RagRequest ragRequest
            ) {
        if (ragRequest == null || ragRequest.question().isBlank()) {
            throw new IllegalArgumentException("question is required");
        }

        return ragTestService.ask(ragRequest.question());
    }

    public record RagRequest(String question){}
}
