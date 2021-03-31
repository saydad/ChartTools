package com.self.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户在特定会话中最新的时间线
 * @author liuyong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_conversation_time")
public class UserConversationTimeEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 会话id
     */
    private Long conversationGroupId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 最新的时间线
     */
    private Long timestamp;

    public UserConversationTimeEntity(Long conversationGroupId, Long userId, Long timestamp) {
        this.conversationGroupId = conversationGroupId;
        this.userId = userId;
        this.timestamp = timestamp;
    }
}
