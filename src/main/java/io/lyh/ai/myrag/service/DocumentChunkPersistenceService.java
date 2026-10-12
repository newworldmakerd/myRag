package io.lyh.ai.myrag.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.lyh.ai.myrag.entity.DocumentChunkEntity;
import io.lyh.ai.myrag.entity.DocumentEntity;
import io.lyh.ai.myrag.enums.DocumentProcessStatus;
import io.lyh.ai.myrag.mapper.DocumentChunkMapper;
import io.lyh.ai.myrag.mapper.DocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentChunkPersistenceService {

    private final DocumentChunkMapper chunkMapper;
    private final DocumentMapper documentMapper;

    @Transactional(rollbackFor = Exception.class)
    public void saveChunks(
            Long documentId,
            List<DocumentChunkEntity> chunks) {

        chunkMapper.insert(chunks);

        int updated = documentMapper.update(
                null,
                new LambdaUpdateWrapper<DocumentEntity>()
                        .eq(DocumentEntity::getId, documentId)
                        .eq(DocumentEntity::getStatus, DocumentProcessStatus.PROCESSING.name())
                        .set(DocumentEntity::getChunkCount, chunks.size())
                        .set(DocumentEntity::getStatus, DocumentProcessStatus.CHUNKED.name())
        );

        if (updated != 1) {
            throw new IllegalStateException(
                    "Document status changed during chunk persistence"
            );
        }
    }
}
