package io.lyh.ai.myrag.rag;

import io.lyh.ai.myrag.dto.RagAnswer;
import io.lyh.ai.myrag.dto.RagQuery;
import io.lyh.ai.myrag.service.RagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RagServiceTest {

    @Autowired
    private RagService ragService;

    @Test
    void testChat() {

        RagAnswer result = ragService.chat(
                new RagQuery(
                        1L,
                        "myRag 使用什么 Embedding 模型？向量维度是多少？",
                        5
                )
        );

        System.out.println("回答：" + result.answer());
        System.out.println("来源：" + result.sources());
    }
}
