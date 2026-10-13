package io.lyh.ai.myrag.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.lyh.ai.myrag.embedding.EmbeddingService;
import io.lyh.ai.myrag.entity.DocumentChunkEntity;
import io.lyh.ai.myrag.entity.DocumentEntity;
import io.lyh.ai.myrag.entity.VectorRecord;
import io.lyh.ai.myrag.enums.DocumentProcessStatus;
import io.lyh.ai.myrag.mapper.DocumentChunkMapper;
import io.lyh.ai.myrag.mapper.DocumentMapper;
import io.lyh.ai.myrag.service.VectorIndexService;
import io.lyh.ai.myrag.storage.VectorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class VectorIndexServiceImpl implements VectorIndexService {

    private final DocumentMapper documentMapper;

    private final DocumentChunkMapper documentChunkMapper;

    private final EmbeddingService embeddingService;

    private final VectorStore vectorStore;

    @Override
    public void indexDocument(Long documentId) {
        DocumentEntity document = documentMapper.selectById(documentId);

        if (document == null) {
            throw new IllegalArgumentException("document not found");
        }

        int updated = documentMapper.update(
                null,
                Wrappers.<DocumentEntity>lambdaUpdate()
                        .eq(DocumentEntity::getId, documentId)
                        .eq(DocumentEntity::getStatus, DocumentProcessStatus.CHUNKED.name())
                        .set(DocumentEntity::getStatus, DocumentProcessStatus.INDEXING.name())
        );

        if (updated != 1) {
            throw new IllegalArgumentException("document can not be indexed");
        }

        try {
            List<DocumentChunkEntity> chunks = documentChunkMapper.selectList(
                    Wrappers.<DocumentChunkEntity>lambdaQuery()
                            .eq(DocumentChunkEntity::getDocumentId, documentId)
                            .orderByAsc(DocumentChunkEntity::getChunkIndex)
            );

            if (chunks.isEmpty()) {
                throw new IllegalArgumentException("chunk not found");
            }

            indexChunks(document, chunks);

            int completed = documentMapper.update(
                    null,
                    Wrappers.<DocumentEntity>lambdaUpdate()
                            .eq(DocumentEntity::getId, documentId)
                            .eq(DocumentEntity::getStatus, DocumentProcessStatus.INDEXING.name())
                            .set(DocumentEntity::getStatus, DocumentProcessStatus.COMPLETED.name())
                            .set(DocumentEntity::getErrorMessage, null));

            if (completed != 1) {
                throw new IllegalArgumentException("document can not be indexed");
            }
        }catch (Exception e){
            log.error("文档向量索引失败，documentId={}", documentId, e);

            // 索引失败后恢复为 CHUNKED，允许重新索引
            int recovered = documentMapper.update(
                    null,
                    Wrappers.<DocumentEntity>lambdaUpdate()
                            .eq(DocumentEntity::getId, documentId)
                            .eq(DocumentEntity::getStatus, "INDEXING")
                            .set(DocumentEntity::getStatus, "CHUNKED")
                            .set(DocumentEntity::getErrorMessage, e.getMessage())
            );

            if (recovered != 1) {
                log.warn("文档索引状态恢复未成功，documentId={}", documentId);
            }

            throw new IllegalStateException("文档向量索引失败", e);
        }
    }

    private void indexChunks(DocumentEntity documentEntity,
                             List<DocumentChunkEntity> chunks) {
        final int batchSize = 10;

        for (int start = 0; start < chunks.size(); start += batchSize) {
            List<DocumentChunkEntity> batch = chunks.subList(
                    start, Math.min(chunks.size(), start + batchSize)
            );

            List<String> texts = batch.stream()
                    .map(DocumentChunkEntity::getContent)
                    .toList();

            List<float[]> vectors = embeddingService.embed(texts);

            if (vectors.size() != batch.size()) {
                throw new IllegalArgumentException("chunks size not match");
            }

            List<VectorRecord> records = getVectorRecords(documentEntity, batch, vectors);
            vectorStore.upsert(records);
        }
    }

    @NotNull
    private static List<VectorRecord> getVectorRecords(DocumentEntity documentEntity, List<DocumentChunkEntity> batch, List<float[]> vectors) {
        List<VectorRecord> records = new ArrayList<>(batch.size());

        for (int i = 0; i < batch.size(); i++) {
            DocumentChunkEntity chunk = batch.get(i);

            records.add(new VectorRecord(
                    chunk.getVectorId(),
                    documentEntity.getKnowledgeBaseId(),
                    documentEntity.getId(),
                    chunk.getId(),
                    chunk.getChunkIndex(),
                    chunk.getContent(),
                    vectors.get(i)
            ));
        }
        return records;
    }
}
