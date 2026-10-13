package io.lyh.ai.myrag.storage;

import io.lyh.ai.myrag.dto.VectorSearchResult;
import io.lyh.ai.myrag.entity.VectorRecord;
import io.lyh.ai.myrag.filter.QdrantFilters;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QueryFactory;
import io.qdrant.client.VectorInputFactory;
import io.qdrant.client.grpc.Common.Filter;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;
import io.qdrant.client.grpc.Points.QueryPoints;
import io.qdrant.client.grpc.Points.ScoredPoint;
import io.qdrant.client.grpc.Points.PointStruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;

@Service
@RequiredArgsConstructor
public class QdrantVectorStore implements VectorStore {

    private final QdrantClient qdrantClient;

    private static final String COLLECTION = "java_knowledge";

    @Override
    public void upsert(List<VectorRecord> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        List<PointStruct> points = records.stream()
                .map(this::toPoint)
                .toList();

        try {
            qdrantClient.upsertAsync(
                    COLLECTION,
                    points
            ).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private PointStruct toPoint(VectorRecord record) {

        return PointStruct.newBuilder()
                .setId(id(record.vectorId()))
                .setVectors(vectors(record.vector()))
                .putPayload("knowledge_base_id", value(record.knowledgeBaseId()))
                .putPayload("document_id", value(record.documentId()))
                .putPayload("chunk_id", value(record.chunkId()))
                .putPayload("chunk_index", value(record.chunkIndex()))
                .putPayload("content", value(record.content()))
                .build();
    }

    @Override
    public void delete(List<UUID> vectorIds) {

    }

    @Override
    public List<VectorSearchResult> search(Long knowledgeBaseId, float[] queryVector, int topK) {
        if (knowledgeBaseId == null) {
            throw new IllegalArgumentException("knowledgeBaseId can not be null");
        }
        if (queryVector == null || queryVector.length != 1024) {
            throw new IllegalArgumentException("queryVector can not be null");
        }
        if (topK < 0) {
            throw new IllegalArgumentException("topK can not be negative");
        }
        Filter filter = QdrantFilters.knowledgeBase(knowledgeBaseId);

        QueryPoints request = QueryPoints.newBuilder()
                .setCollectionName("java_knowledge")
                .setQuery(QueryFactory.nearest(VectorInputFactory.vectorInput(queryVector)))
                .setFilter(filter)
                .setLimit(topK)
                .setWithPayload(
                        Points.WithPayloadSelector.newBuilder()
                                .setEnable(true)
                                .build()
                )
                .build();
        try {
            List<ScoredPoint> points = qdrantClient.queryAsync(request).get();
            return points.stream()
                    .map(this::toVectorSearchResult)
                    .toList();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Qdrant 检索被中断", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Qdrant 检索失败", e);
        }
    }

    private VectorSearchResult toVectorSearchResult(ScoredPoint point) {
        Map<String, JsonWithInt.Value> payload = point.getPayloadMap();

        return new VectorSearchResult(
                UUID.fromString(point.getId().getUuid()),
                payload.get("document_id").getIntegerValue(),
                payload.get("chunk_id").getIntegerValue(),
                Math.toIntExact(payload.get("chunk_id").getIntegerValue()),
                payload.get("content").getStringValue(),
                point.getScore()
        );
    }
}
