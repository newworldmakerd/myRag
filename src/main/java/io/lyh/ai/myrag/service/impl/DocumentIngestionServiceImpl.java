package io.lyh.ai.myrag.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.lyh.ai.myrag.chunker.TextChunker;
import io.lyh.ai.myrag.entity.DocumentChunkEntity;
import io.lyh.ai.myrag.entity.DocumentEntity;
import io.lyh.ai.myrag.enums.DocumentProcessStatus;
import io.lyh.ai.myrag.mapper.DocumentChunkMapper;
import io.lyh.ai.myrag.mapper.DocumentMapper;
import io.lyh.ai.myrag.parser.DocumentParser;
import io.lyh.ai.myrag.parser.ParsedDocument;
import io.lyh.ai.myrag.service.DocumentChunkPersistenceService;
import io.lyh.ai.myrag.service.DocumentIngestionService;
import io.lyh.ai.myrag.storage.FileStorage;
import io.lyh.ai.myrag.util.Sha256Utils;
import lombok.RequiredArgsConstructor;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentIngestionServiceImpl implements DocumentIngestionService {

    private final DocumentMapper documentMapper;
    private final DocumentChunkMapper chunkMapper;
    private final FileStorage fileStorage;
    private final DocumentParser documentParser;
    private final TextChunker textChunker;
    private final DocumentChunkPersistenceService persistenceService;

    @Override
    public void ingest(Long documentId) throws TikaException, IOException {
        DocumentEntity documentEntity = documentMapper.selectById(documentId);

        if (documentEntity == null) {
            throw new TikaException("Document not found" + documentId);
        }

        int updated = documentMapper.update(
                null,
                new LambdaUpdateWrapper<DocumentEntity>()
                        .eq(DocumentEntity::getId, documentId)
                        .eq(DocumentEntity::getStatus, DocumentProcessStatus.PENDING.name())
                        .set(DocumentEntity::getStatus, DocumentProcessStatus.PROCESSING.name())
        );

        if (updated != 1) {
            throw new IllegalStateException("document is in processing or not exists");
        }

        try (InputStream fileStream = fileStorage.download(documentEntity.getFilePath())) {
            ParsedDocument parsedDocument = documentParser.parse(fileStream, documentEntity.getFileName());

            List<String> splits = textChunker.split(parsedDocument.content());

            if (splits == null || splits.isEmpty()) {
                throw new IllegalStateException(
                        "document has no extractable content"
                );
            }

            List<DocumentChunkEntity> documentChunkEntities = new ArrayList<>();

            for (int i = 0; i < splits.size(); i++) {
                String content = splits.get(i);

                DocumentChunkEntity chunk = new DocumentChunkEntity();

                chunk.setDocumentId(documentId);
                chunk.setChunkIndex(i);
                chunk.setContent(content);

                chunk.setContentHash(Sha256Utils.calculateSha256(content));
                chunk.setVectorId(UUID.randomUUID());

                documentChunkEntities.add(chunk);
            }

            persistenceService.saveChunks(documentId, documentChunkEntities);
        }catch (Exception e){
            documentMapper.update(
                    null,
                    new LambdaUpdateWrapper<DocumentEntity>()
                            .eq(DocumentEntity::getId, documentId)
                            .eq(DocumentEntity::getStatus,
                                    DocumentProcessStatus.PROCESSING.name())
                            .set(DocumentEntity::getStatus,
                                    DocumentProcessStatus.FAILED.name())
                            .set(DocumentEntity::getErrorMessage,
                                    e.getMessage())
            );

            throw new IllegalStateException(
                    "document ingestion failed: " + documentId, e
            );
        }
    }
}
