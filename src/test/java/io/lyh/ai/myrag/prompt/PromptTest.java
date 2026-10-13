package io.lyh.ai.myrag.prompt;

import io.lyh.ai.myrag.dto.RetrievalResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
public class PromptTest {

    @Test
    void testPromptBuilder(){
        List<RetrievalResult> results = List.of(
                new RetrievalResult(
                        3L,
                        12L,
                        0,
                        "RAG 是检索增强生成技术。",
                        0.85f
                )
        );

        PromptBuilder builder = new PromptBuilder();

        String prompt = builder.build(
                "什么是RAG？",
                results
        );

        System.out.println(prompt);

        assertTrue(prompt.contains("什么是RAG？"));
        assertTrue(prompt.contains("检索增强生成技术"));
        assertTrue(prompt.contains("[资料1]"));
    }
}
