package io.lyh.ai.myrag.service.impl;

import io.lyh.ai.myrag.dto.RagAnswer;
import io.lyh.ai.myrag.dto.RagQuery;
import io.lyh.ai.myrag.dto.RetrievalQuery;
import io.lyh.ai.myrag.dto.RetrievalResult;
import io.lyh.ai.myrag.prompt.PromptBuilder;
import io.lyh.ai.myrag.retrieval.Retriever;
import io.lyh.ai.myrag.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultRagService implements RagService {

    private final Retriever retriever;
    private final PromptBuilder promptBuilder;
    private final ChatModel chatModel;

    @Override
    public RagAnswer chat(RagQuery query) {
        List<RetrievalResult> results =
                retriever.retrieve(
                        new RetrievalQuery(
                                query.knowledgeBaseId(),
                                query.question(),
                                query.topK()
                        )
                );

        if (results.isEmpty()) {
            return new RagAnswer(
                    "知识库中没有找到相关资料。",
                    List.of()
            );
        }

        String prompt = promptBuilder.build(
                query.question(),
                results
        );

        String answer = chatModel.call(prompt);

        return new RagAnswer(answer, results);
    }
}
