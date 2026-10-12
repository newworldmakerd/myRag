package io.lyh.ai.myrag.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lyh.ai.myrag.entity.DocumentChunkEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DocumentChunkMapper extends BaseMapper<DocumentChunkEntity> {
}
