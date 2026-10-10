package io.lyh.ai.myrag.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.lyh.ai.myrag.dto.KnowledgeBaseCreateRequest;
import io.lyh.ai.myrag.dto.KnowledgeBaseResponse;
import io.lyh.ai.myrag.entity.KnowledgeBaseEntity;
import io.lyh.ai.myrag.mapper.KnowledgeBaseMapper;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class KnowledgeBaseService {

    private final KnowledgeBaseMapper knowledgeBaseMapper;

    public KnowledgeBaseService(KnowledgeBaseMapper knowledgeBaseMapper) {
        this.knowledgeBaseMapper = knowledgeBaseMapper;
    }

    public KnowledgeBaseResponse create(
            KnowledgeBaseCreateRequest request
    ){
        if (request == null
                || request.name() == null
                || request.name().isBlank()) {
            throw new IllegalArgumentException("知识库名称不能为空");
        }

        String name = request.name().trim();

        if (name.length() > 100) {
            throw new IllegalArgumentException("知识库名称不能超过100个字符");
        }

        if (request.description() != null
                && request.description().length() > 500) {
            throw new IllegalArgumentException("知识库描述不能超过500个字符");
        }

        KnowledgeBaseEntity entity = new KnowledgeBaseEntity();
        entity.setName(name);
        entity.setDescription(request.description());
        entity.setStatus((short) 1);

        OffsetDateTime now = OffsetDateTime.now();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        knowledgeBaseMapper.insert(entity);

        return getById(entity.getId());
    }

    public KnowledgeBaseResponse getById(Long id) {
        KnowledgeBaseEntity entity = knowledgeBaseMapper.selectById(id);

        if (entity == null) {
            throw new NoSuchElementException("知识库不存在：" + id);
        }

        return convert(entity);
    }

    public List<KnowledgeBaseResponse> list() {
        return knowledgeBaseMapper.selectList(
                new LambdaQueryWrapper<KnowledgeBaseEntity>()
                        .orderByDesc(KnowledgeBaseEntity::getCreateTime)
                        .orderByDesc(KnowledgeBaseEntity::getId)
        ).stream().map(this::convert).toList();
    }

    private KnowledgeBaseResponse convert(KnowledgeBaseEntity entity) {
        return new KnowledgeBaseResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreateTime()
        );
    }
}
