package com.self.repository;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.self.repository.entity.UserConversationTimeEntity;
import com.self.repository.mapper.UserSessionMapper;

/**
 * 用户会话时间线-操作
 * @author liuyong
 */
@Component
public class UserSessionDao {

    @Resource
    private UserSessionMapper userSessionMapper;

    /**
     * 批量查询指定用户的最新会话时间线
     * @param userId 用户id
     * @param conversationGroupIds 会话id列表
     * @return 用户会话时间线信息
     */
    public List<UserConversationTimeEntity> batchGetConversationSpecifyUser(long userId, Set<Long> conversationGroupIds) {
        return userSessionMapper.selectList(Wrappers.<UserConversationTimeEntity>lambdaQuery()
                .eq(UserConversationTimeEntity::getUserId, userId)
                .in(UserConversationTimeEntity::getConversationGroupId, conversationGroupIds));
    }

    /**
     * 存在则更新，不存在则插入
     */
    public void updateOrSave(UserConversationTimeEntity entity) {
        UserConversationTimeEntity existEntity = userSessionMapper.selectOne(Wrappers.<UserConversationTimeEntity>lambdaQuery()
                .eq(UserConversationTimeEntity::getUserId, entity.getUserId())
                .eq(UserConversationTimeEntity::getConversationGroupId, entity.getConversationGroupId())
                .last("limit 1"));

        if (Objects.nonNull(existEntity)) {
            existEntity.setTimestamp(entity.getTimestamp());
            userSessionMapper.update(existEntity, Wrappers.<UserConversationTimeEntity>lambdaUpdate().eq(
                    UserConversationTimeEntity::getId, existEntity.getId()));
        } else {
            userSessionMapper.insert(entity);
        }
    }
}
