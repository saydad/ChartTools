package com.self.beans.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息查询参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageSearchParam {
    /**
     * 会话id
     */
    private long conversationId;
    /**
     * 当前用户id
     */
    private long curUserId;
    /**
     * 查询消息的时间界限
     */
    private long timestamp;
    /**
     * 是否向下查询
     */
    private boolean down;
}
