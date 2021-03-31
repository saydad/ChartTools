package com.self.constant;

/**
 * token 状态
 * @author liuyong
 */
public enum TokenState {
    /**
     * 正常
     */
    VALID(0),
    /**
     * 无效
     */
    INVALID(1),
    /**
     * 过期
     */
    EXPIRE(2)
    ;

    public int code;

    TokenState(int code) {
        this.code = code;
    }
}
