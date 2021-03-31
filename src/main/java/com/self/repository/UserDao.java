package com.self.repository;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.self.repository.entity.UserEntity;
import com.self.repository.mapper.UserMapper;

/**
 * 用户相关数据库操作
 * @author liuyong
 */
@Component
public class UserDao {

    @Resource
    private UserMapper userMapper;

    /**
     * 手机号查询
     * @param phone 加密后的手机号
     * @return 用户信息
     */
    public UserEntity getByPhone(String phone) {
        return userMapper.selectOne(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getPhoneNum, phone).last("limit 1"));
    }

    /**
     * 插入数据
     * @param entity 实体
     * @return 插入记录数量
     */
    public int insert(UserEntity entity) {
        long now = System.currentTimeMillis();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        return userMapper.insert(entity);
    }

    /**
     * 查询用户列表
     * @param query 查询条件
     * @return 用户列表
     */
    public List<UserEntity> serach(String query) {
        LambdaQueryWrapper<UserEntity> queryWrapper = Wrappers.<UserEntity> lambdaQuery();
        if (StringUtils.isNoneBlank(query)) {
            queryWrapper.like(UserEntity::getPhoneNum, query).or().like(UserEntity::getNickName, query);
        }
        return userMapper.selectList(queryWrapper);
    }

    /**
     * 通过id批量查询
     * @param ids 主键
     * @return 用户信息
     */
    public List<UserEntity> batchGetByIds(Set<Long> ids) {
        return userMapper.selectList(Wrappers.<UserEntity>lambdaQuery().in(UserEntity::getId, ids));
    }
}
