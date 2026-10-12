package io.lyh.ai.myrag.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.lyh.ai.myrag.dto.DocumentUploadResponse;
import io.lyh.ai.myrag.entity.DocumentEntity;
import io.lyh.ai.myrag.entity.KnowledgeBaseEntity;
import io.lyh.ai.myrag.mapper.DocumentMapper;
import io.lyh.ai.myrag.mapper.KnowledgeBaseMapper;
import io.lyh.ai.myrag.parser.DocumentParser;
import io.lyh.ai.myrag.storage.FileStorage;
import io.lyh.ai.myrag.util.Sha256Utils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class DocumentService {

    private final DocumentMapper documentMapper;
    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final DocumentParser documentParser;
    private final FileStorage fileStorage;

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;


    public DocumentService(
            DocumentMapper documentMapper,
            KnowledgeBaseMapper knowledgeBaseMapper,
            DocumentParser documentParser,
            FileStorage fileStorage
    ) {
        this.documentMapper = documentMapper;
        this.knowledgeBaseMapper = knowledgeBaseMapper;
        this.documentParser = documentParser;
        this.fileStorage = fileStorage;
    }

    public DocumentUploadResponse upload(
            Long knowledgeBaseId,
            MultipartFile file
    ) throws Exception {
        KnowledgeBaseEntity kb = knowledgeBaseMapper.selectById(knowledgeBaseId);

        if (kb == null || kb.getStatus() != 1) {
            throw new IllegalArgumentException("knowledgeBase not exist");
        }

        String fileName = detectAndGetFileName(file);
        String extension = detectExtensionAndGet(file, fileName);
        String fileHash = checkExistedAndGetSha256(file, kb);

        String objectKey = String.format("myrag/knowledge-bases/%d/documents/%s/original.%s", knowledgeBaseId, UUID.randomUUID(), extension);

        try (InputStream inputStream = file.getInputStream()) {
            fileStorage.upload(objectKey, inputStream, file.getSize());
        }

        DocumentEntity entity = new DocumentEntity();
        entity.setKnowledgeBaseId(knowledgeBaseId);
        entity.setFileName(fileName);
        entity.setFileType(extension);
        entity.setFileSize(file.getSize());
        entity.setFileHash(fileHash);
        entity.setFilePath(objectKey);
        entity.setStatus("PENDING");
        entity.setChunkCount(0);

        try {
            documentMapper.insert(entity);
        } catch (RuntimeException e) {
            // 数据库写入失败，尝试清理 OSS 对象
            try {
                fileStorage.delete(objectKey);
            } catch (Exception cleanupException) {
                e.addSuppressed(cleanupException);
            }
            throw e;
        }

        return new DocumentUploadResponse(
                entity.getId(),
                knowledgeBaseId,
                fileName,
                entity.getStatus()
        );
    }

    private String checkExistedAndGetSha256(MultipartFile file, KnowledgeBaseEntity kb) throws IOException {
        String fileHash = Sha256Utils.calculateFileSha256(file);

        Long count = documentMapper.selectCount(
                new LambdaQueryWrapper<DocumentEntity>()
                        .eq(DocumentEntity::getKnowledgeBaseId, kb.getId())
                        .eq(DocumentEntity::getFileHash, fileHash)
        );

        if (count > 0) {
            throw new IllegalArgumentException("file already exist");
        }
        return fileHash;
    }

    private String detectFile(MultipartFile file) throws Exception {
        String fileName = detectAndGetFileName(file);
        fileName = fileName.substring(fileName.lastIndexOf('/') + 1);

        if (fileName.length() > 255) {
            throw new IllegalArgumentException("filename is too large");
        }

        return fileName;
    }

    @NotNull
    private String detectExtensionAndGet(MultipartFile file, String fileName) throws IOException {
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex <= 0 || dotIndex == fileName.length() - 1) {
            throw new IllegalArgumentException("filename suffix is illegal");
        }

        String extension = fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);

        String mimeType;

        try (InputStream inputStream = file.getInputStream()) {
            mimeType = documentParser.detect(inputStream, fileName);
        }

        Map<String, Set<String>> allowedTypes = Map.of(
                "pdf", Set.of("application/pdf"),
                "docx", Set.of(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                ),
                "txt", Set.of("text/plain"),
                "md", Set.of("text/plain", "text/markdown")
        );

        Set<String> expectedTypes = allowedTypes.get(extension);

        if (expectedTypes == null || !expectedTypes.contains(mimeType)) {
            throw new IllegalArgumentException(
                    "文件类型不支持或文件内容与扩展名不匹配：" + mimeType
            );
        }
        return extension;
    }

    @NotNull
    private static String detectAndGetFileName(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("file size is too large");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("filename is empty");
        }

        String fileName = originalFilename.replace('\\', '/');
        return fileName;
    }

}
