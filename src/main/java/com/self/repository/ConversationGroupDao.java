package com.self.repository;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.self.repository.entity.ConversationGroupEntity;
import com.self.repository.mapper.ConversationGroupMapper;

@Component
public class ConversationGroupDao {

    @Resource
    private ConversationGroupMapper conversationGroupMapper;

    /**
     * 插入实体
     */
    public void insertEntity(ConversationGroupEntity entity) {
        conversationGroupMapper.insert(entity);
    }

    /**
     * 通过id批量获取
     * @param ids 主键
     * @return 会话信息
     */
    public List<ConversationGroupEntity> batchGetByIds(Set<Long> ids) {
        return conversationGroupMapper.selectBatchIds(ids);
    }
}
