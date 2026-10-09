package io.lyh.ai.myrag.controller;

import io.lyh.ai.myrag.core.model.RetrievalQuery;
import io.lyh.ai.myrag.core.model.RetrievedChunk;
import io.lyh.ai.myrag.core.spi.Retriever;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/retrieval")
public class RetrievalController {

    public final Retriever retriever;

    public RetrievalController(Retriever retriever) {
        this.retriever = retriever;
    }

    @PostMapping("/search")
    public List<RetrievedChunk> search(@RequestBody RetrievalQuery query) {

        if (query.query() == null || query.query().isEmpty()) {
            throw new IllegalArgumentException("query is null or empty");
        }

        if (query.topK() <= 1 ||  query.topK() > 100) {
            throw new IllegalArgumentException("topK is out of range");
        }

        return retriever.retrieve(query);
    }
}
