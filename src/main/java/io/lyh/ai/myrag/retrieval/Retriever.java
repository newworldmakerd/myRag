package io.lyh.ai.myrag.retrieval;

import io.lyh.ai.myrag.dto.RetrievalQuery;
import io.lyh.ai.myrag.dto.RetrievalResult;

import java.util.List;

public interface Retriever {
    List<RetrievalResult> retrieve(RetrievalQuery query);
}
