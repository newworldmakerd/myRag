package io.lyh.ai.myrag.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lyh.ai.myrag.entity.DocumentEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DocumentMapper extends BaseMapper<DocumentEntity> {
}
