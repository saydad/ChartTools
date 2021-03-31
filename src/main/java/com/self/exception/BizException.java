package com.self.exception;

import com.self.constant.ErrorDefinition;

import lombok.Getter;

/**
 * 业务异常
 * @author liuyong
 */
@Getter
public class BizException extends RuntimeException{

    private final int code;
    private final String msg;


    public BizException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BizException(ErrorDefinition errorDefinition) {
        this.code = errorDefinition.code;
        this.msg = errorDefinition.msg;
    }
}
