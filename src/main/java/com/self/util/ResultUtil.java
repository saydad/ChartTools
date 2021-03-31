package com.self.util;

import java.util.function.Supplier;

import com.self.exception.BizException;
import com.self.beans.vo.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * 返回结果包装&异常捕获
 * @author liuyong
 */
@Slf4j
public class ResultUtil {

    public static <T> Result<T> toResult(Supplier<T> handler) {
        try {
            T result = handler.get();
            return Result.success(result);
        } catch (BizException bizException) {
            return Result.fail(bizException.getCode(), bizException.getMsg());
        } catch (Exception e) {
            log.error("系统异常: ", e);
            return Result.fail(-1, "系统异常");
        }
    }

    public static Result<Void> toVoid(Supplier<Void> handler) {
        try {
            handler.get();
            return Result.success(null);
        } catch (BizException bizException) {
            return Result.fail(bizException.getCode(), bizException.getMsg());
        } catch (Exception e) {
            log.error("系统异常: ", e);
            return Result.fail(-1, "系统异常");
        }
    }
}
