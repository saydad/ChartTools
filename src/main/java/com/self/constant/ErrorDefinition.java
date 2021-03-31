package com.self.constant;

/**
 * 错误定义
 * @author liuyong
 */
public enum ErrorDefinition {
    /**
     * 错误枚举定义
     */
    PARAM_MISS(400001, "参数缺失"),
    EXIST_PHONE(400002, "手机号已存在"),
    INVALID_PHONE(400003, "不存在的手机号"),
    INVALID_PASSWORD(400004, "密码不正确"),


    INSERT_USER_FAIL(500001, "用户创建失败")
    ;

    public int code;
    public String msg;

    ErrorDefinition(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
