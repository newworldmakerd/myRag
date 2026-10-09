package io.lyh.ai.myrag.core.spi;

import io.lyh.ai.myrag.core.model.RetrievalQuery;
import io.lyh.ai.myrag.core.model.RetrievedChunk;

import java.util.List;

public interface Retriever {

    List<RetrievedChunk> retrieve(RetrievalQuery query);
}
