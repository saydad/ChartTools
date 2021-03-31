package com.self.repository;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.self.repository.entity.MessageEntity;
import com.self.repository.mapper.MessageMapper;

@Repository
public class MessageDao {

    @Resource
    private MessageMapper messageMapper;

    /**
     * 查询特定时间向下最近一条消息
     * @param conversationGroupId 会话id
     * @param timestamp 时间戳
     * @return 消息id
     */
    public List<Object> bachGetCurTimestampNextMsg(Long conversationGroupId, long timestamp) {
        QueryWrapper<MessageEntity> queryWrapper = Wrappers.<MessageEntity> query().select("min(id) as id")
                .eq("conversation_group_id", conversationGroupId)
                .gt("timestamp", timestamp)
                .groupBy("conversation_group_id");
        return messageMapper.selectObjs(queryWrapper);
    }

    /**
     * 查询特定时间向上最近一条消息
     * @param conversationGroupId 会话id
     * @param timestamp 时间戳
     * @return 消息id
     */
    public List<Object> batchGetCurTimestampPreviousMsg(Long conversationGroupId, long timestamp) {
        QueryWrapper<MessageEntity> queryWrapper = Wrappers.<MessageEntity> query().select("max(id) as id")
                .eq("conversation_group_id", conversationGroupId)
                .le("timestamp", timestamp)
                .groupBy("conversation_group_id");
        return messageMapper.selectObjs(queryWrapper);
    }

    /**
     * 通过id批量查询消息
     * @param ids 主键
     * @return 消息
     */
    public List<MessageEntity> batchGetByIds(Set<Long> ids) {
        return messageMapper.selectBatchIds(ids);
    }

    /**
     * 查询指定会话指定时间戳的消息列表
     * @param conversationGroupId 会话id
     * @param timestamp 时间戳
     * @param down 是否向下查询
     * @return 消息列表
     */
    public List<MessageEntity> getMsgBySpecifySession(long conversationGroupId, long timestamp, boolean down) {
        LambdaQueryWrapper<MessageEntity> queryWrapper = Wrappers.<MessageEntity> lambdaQuery()
                .eq(MessageEntity::getConversationGroupId, conversationGroupId);
        if (down) {
            queryWrapper.ge(MessageEntity::getTimestamp, timestamp);
        } else {
            queryWrapper.lt(MessageEntity::getTimestamp, timestamp).orderByDesc(MessageEntity::getTimestamp).last("limit 10");
        }

        return messageMapper.selectList(queryWrapper);
    }

    /**
     * 插入消息
     */
    public int insertMsg(MessageEntity entity) {
        return messageMapper.insert(entity);
    }
}
