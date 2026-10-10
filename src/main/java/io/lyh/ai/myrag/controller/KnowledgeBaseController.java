package io.lyh.ai.myrag.controller;

import io.lyh.ai.myrag.dto.KnowledgeBaseCreateRequest;
import io.lyh.ai.myrag.dto.KnowledgeBaseResponse;
import io.lyh.ai.myrag.service.KnowledgeBaseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/knowledge-bases")
public class KnowledgeBaseController {
    private final KnowledgeBaseService service;

    public KnowledgeBaseController(KnowledgeBaseService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public KnowledgeBaseResponse create(
            @RequestBody KnowledgeBaseCreateRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public KnowledgeBaseResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<KnowledgeBaseResponse> list() {
        return service.list();
    }
}
