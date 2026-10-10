package io.lyh.ai.myrag.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@TableName("rag_document")
public class DocumentEntity {


    @TableId(type = IdType.AUTO)
    private Long id;

    private Long knowledgeBaseId;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String fileHash;

    private String filePath;

    private String status;

    private Integer chunkCount;

    private String errorMessage;

    private OffsetDateTime createTime;

    private OffsetDateTime updateTime;
}
