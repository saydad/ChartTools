package com.self.beans.msg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分布式会话消息
 * @author liuyong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionMsg {
    /**
     * 操作类型
     * @see com.self.constant.MsgType
     */
    private int actionType;
    /**
     * 目标用户id
     */
    private long targetUserId;
    /**
     * 聊天会话id -- 发送消息时存在
     */
    private long conversationGroupId;
    /**
     * 发送的消息的机器标识
     */
    private String machine;
}
