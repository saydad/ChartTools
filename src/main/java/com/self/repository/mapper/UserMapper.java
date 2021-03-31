package com.self.repository.mapper;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.self.repository.entity.UserEntity;

/**
 * 用户数据库操作
 * @author liuyong
 */
@Repository
public interface UserMapper extends BaseMapper<UserEntity> {
}
