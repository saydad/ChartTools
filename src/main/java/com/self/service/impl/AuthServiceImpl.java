package com.self.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.self.constant.TokenState;
import com.self.service.AuthService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    private static final String TOKEN_REDIS_KEY_PREFIX = "chart_token_";

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public int authToken(String token) {
        if (StringUtils.isBlank(token)) {
            return TokenState.INVALID.code;
        }

        String rawStr = new String(Base64.getDecoder().decode(token));
        String[] splitContent = rawStr.split("\\.");
        if (splitContent.length != 2) {
            log.info("校验token, 拆分后长度不为2 {}", token);
            return TokenState.INVALID.code;
        }

        String redisKey = TOKEN_REDIS_KEY_PREFIX + splitContent[0];
        Boolean exist = redisTemplate.hasKey(redisKey);
        if (Objects.nonNull(exist) && exist) {
            return TokenState.VALID.code;
        }

        log.info("校验token, 用户 {} 登录过期", splitContent[0]);
        return TokenState.EXPIRE.code;
    }

    @Override
    public String generateToken(long userId) {
        long now = System.currentTimeMillis();
        String rawStr = userId + "." + now;

        String token = Base64.getEncoder().encodeToString(rawStr.getBytes(StandardCharsets.UTF_8));
        ValueOperations<String, String> strOperate = redisTemplate.opsForValue();

        String redisKey = TOKEN_REDIS_KEY_PREFIX + userId;
        strOperate.set(redisKey, token);
        redisTemplate.expire(redisKey, 1, TimeUnit.DAYS);

        return token;
    }
}
