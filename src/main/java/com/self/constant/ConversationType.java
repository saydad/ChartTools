package com.self.constant;

/**
 * 会话类型
 * @author liuyong
 */
public enum ConversationType {
    /**
     * 单聊
     */
    SINGLE_CHART(1),
    /**
     * 群聊
     */
    GROUP_CHART(2)
    ;

    public int type;

    ConversationType(int type) {
        this.type = type;
    }
}
