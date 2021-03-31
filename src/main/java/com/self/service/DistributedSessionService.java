package com.self.service;

/**
 * 分布式会话session操作
 * @author liuyong
 */
public interface DistributedSessionService {

    /**
     * 通知用户有新消息
     * @param conversationId 会话id
     * @param userId 目标用户id
     */
    void notifyUser(long conversationId, long userId);

    /**
     * 防止session已经无效,但是在其他机器被被保留
     * @param userId 用户id
     */
    void clearUser(long userId);
}
