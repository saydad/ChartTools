package com.self.beans.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会话信息
 * @author liuyong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionInfo {
    /**
     * 会话id
     */
    private long id;
    /**
     * 会话名称
     */
    private String name;
    /**
     * 指定用户需要看到的最新一条消息
     */
    private String lastMsg;
    /**
     * 上述消息的时间戳
     */
    private long timestamp;
}
