package io.lyh.ai.myrag.controller;


import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/embedding")
public class EmbeddingController {

    private final EmbeddingModel embeddingModel;

    public EmbeddingController(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @PostMapping("/test")
    public Map<String,Object> test(@RequestBody TextRequest request){
        float[] vector = embeddingModel.embed(request.text());
        return Map.of(
                "dimension",vector.length,
                "preview", Arrays.copyOf(vector, 10)
        );
    }

    public record TextRequest(String text) {}
}
