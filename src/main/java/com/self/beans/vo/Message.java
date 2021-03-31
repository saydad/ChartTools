package com.self.beans.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    /**
     * 消息id
     */
    private long id;
    /**
     * 发送者名称
     */
    private String user;
    /**
     * 消息正文
     */
    private String msg;
    /**
     * 消息时间戳
     */
    private long timestamp;
    /**
     * 该消息是否为当前用户发送
     */
    private boolean curUser;
}
