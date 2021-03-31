package com.self.constant;

/**
 * 分布式session消息类型
 * @author liuyong
 */

public enum MsgType {
    SEND_MSG(1),
    CLEAR_MSG(2)
    ;

    public int type;

    MsgType(int type) {
        this.type = type;
    }
}
