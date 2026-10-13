package io.lyh.ai.myrag.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.lyh.ai.myrag.handler.PostgreSqlUuidTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@TableName(value = "rag_document_chunk",autoResultMap = true)
@Accessors(chain = true)
public class DocumentChunkEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long documentId;

    private Integer chunkIndex;

    private String content;

    private String contentHash;

    @TableField(
            value = "vector_id",
            typeHandler = PostgreSqlUuidTypeHandler.class,
            jdbcType = JdbcType.OTHER
    )
    private UUID vectorId;

    private Integer tokenCount;

    private OffsetDateTime createTime;
}
