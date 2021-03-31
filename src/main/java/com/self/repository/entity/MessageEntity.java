package com.self.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息实体
 * @author liuyong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("message")
public class MessageEntity {

    @TableId(type=IdType.AUTO)
    private Long id;

    /**
     * 会话id
     */
    private Long conversationGroupId;

    /**
     * 发送用户id
     */
    private Long userId;

    /**
     * 消息正文
     */
    private String msg;

    private long timestamp;

    public MessageEntity(Long conversationGroupId, Long userId, String msg, long timestamp) {
        this.conversationGroupId = conversationGroupId;
        this.userId = userId;
        this.msg = msg;
        this.timestamp = timestamp;
    }
}
