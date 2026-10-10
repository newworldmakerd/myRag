package io.lyh.ai.myrag.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lyh.ai.myrag.entity.KnowledgeBaseEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KnowledgeBaseMapper extends BaseMapper<KnowledgeBaseEntity> {
}
