package io.lyh.ai.myrag.retrieve;

import io.lyh.ai.myrag.dto.RetrievalQuery;
import io.lyh.ai.myrag.dto.RetrievalResult;
import io.lyh.ai.myrag.retrieval.Retriever;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class RetrieveTest {

    @Autowired
    private Retriever retriever;

    @Test
    void testRetrieve() {
        RetrievalQuery query = new RetrievalQuery(
                1L,
                "什么是RAG？它的工作原理是什么？",
                5
        );

        List<RetrievalResult> results =
                retriever.retrieve(query);

        assertFalse(results.isEmpty());

        results.forEach(result ->
                System.out.println(
                        result.score() + " : " + result.content()
                )
        );
    }
}
