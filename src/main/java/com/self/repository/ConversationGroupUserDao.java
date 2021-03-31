package com.self.repository;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.self.repository.entity.ConversationGroupUserEntity;
import com.self.repository.mapper.ConversationGroupUserMapper;

@Component
public class ConversationGroupUserDao {

    @Resource
    private ConversationGroupUserMapper conversationGroupUserMapper;

    /**
     * 插入实体
     */
    public void insertEntities(List<ConversationGroupUserEntity> entities) {
        for (ConversationGroupUserEntity entity : entities) {
            conversationGroupUserMapper.insert(entity);
        }
    }

    /**
     * 获取用户所有的会话
     * @param userId 用户id
     * @return 会话列表
     */
    public List<ConversationGroupUserEntity> batchGetByUserId(long userId) {
        return conversationGroupUserMapper.selectList(Wrappers.<ConversationGroupUserEntity>lambdaQuery().eq(ConversationGroupUserEntity::getUserId, userId));
    }

    /**
     * 根据会话id批量获取
     * @param conversationGroupIds 会话主键
     * @return 会话列表
     */
    public List<ConversationGroupUserEntity> batchGetUserByConversationGroupIds(Set<Long> conversationGroupIds) {
        return conversationGroupUserMapper.selectList(Wrappers.<ConversationGroupUserEntity>lambdaQuery()
                .in(ConversationGroupUserEntity::getConversationGroupId, conversationGroupIds));
    }

    /**
     * 获取指定用户的单聊关系数据
     * @param user1 用户1
     * @param user2 用户2
     * @return 单聊关系数据
     */
    public ConversationGroupUserEntity getSingleChartRelation(long user1, long user2) {
        return conversationGroupUserMapper.getSingleChartRelation(user1, user2);
    }
}
