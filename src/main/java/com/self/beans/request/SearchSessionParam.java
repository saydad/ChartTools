package com.self.beans.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询sessionInfo参数
 * @author liuyong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchSessionParam {
    /**
     * @see com.self.constant.ConversationType
     */
    private int type;
    /**
     * 单聊-目标用户id
     * 群聊-会话id
     */
    private long targetId;
    /**
     * 当前用户id
     */
    private long curUserId;
}
