package com.self.service;

/**
 * 授权服务
 * @author liuyong
 */
public interface AuthService {

    /**
     * token 验证
     * @param token token
     * @return 状态
     */
    int authToken(String token);

    /**
     * token生成
     * @param userId 用户id
     * @return token
     */
    String generateToken(long userId);
}
