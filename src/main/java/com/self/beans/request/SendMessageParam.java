package com.self.beans.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送消息参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageParam {
    /**
     * 会话id
     */
    private long conversationId;
    /**
     * 发送消息的用户id
     */
    private long userId;
    /**
     * 消息正文
     */
    private String msg;
    /**
     * 消息时间戳
     */
    private long timestamp;
}
