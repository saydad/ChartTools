package com.self.service;

import java.util.List;
import java.util.Set;

import com.self.beans.request.MessageSearchParam;
import com.self.beans.request.SearchSessionParam;
import com.self.beans.request.SendMessageParam;
import com.self.beans.vo.Message;
import com.self.beans.vo.SessionInfo;

/**
 * 会话service
 * @author liuyong
 */
public interface ConversationService {
    /**
     * 指定用户的会话列表
     * @param userId 用户id
     * @return 会话信息
     */
    List<SessionInfo> sessionListByUser(long userId);

    /**
     * 新建/查询会话信息
     * @param searchParam 搜索参数
     * @return 会话信息
     */
    SessionInfo acquireSessionInfo(SearchSessionParam searchParam);

    /**
     * 创建聊天会话记录
     * @param type 聊天会话类型
     * @param chartName 聊天会话名称
     * @param chartUsers 聊天会话成员
     * @return 会话信息
     */
    SessionInfo createChart(int type, String chartName, Set<Long> chartUsers);

    /**
     * 查询消息列表
     * @param param 查询列表
     * @return 消息列表
     */
    List<Message> messageList(MessageSearchParam param);

    /**
     * 发送消息
     * @param param 参数
     * @return 是否成功
     */
    boolean sendMsg(SendMessageParam param);
}
