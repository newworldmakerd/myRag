package io.lyh.ai.myrag.controller;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai/knowledge")
public class KnowledgeController {

    private final VectorStore vectorStore;

    public KnowledgeController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostMapping("/add")
    public Map<String,Object> add(@RequestBody TextRequest request){

        Document document = new Document(request.text());

        vectorStore.add(List.of(document));

        return Map.of(
                "success",true,
                "documentId",document.getId()
        );
    }

    @GetMapping("/search")
    public List<Document> search(@RequestParam String query){

        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(5)
                .build();

        return vectorStore.similaritySearch(searchRequest);
    }

    public record TextRequest(String text){}
}
