package io.lyh.ai.myrag.service;

import io.lyh.ai.myrag.dto.RagAnswer;
import io.lyh.ai.myrag.dto.RagQuery;

public interface RagService {
    RagAnswer chat(RagQuery query);
}
