package com.self.repository.mapper;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.self.repository.entity.UserConversationTimeEntity;

/**
 * 用户会话时间线
 * @author liuyong
 */
@Repository
public interface UserSessionMapper extends BaseMapper<UserConversationTimeEntity> {
}
