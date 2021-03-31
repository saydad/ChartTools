package com.self.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.self.beans.vo.SearchedUser;
import com.self.constant.ErrorDefinition;
import com.self.exception.BizException;
import com.self.repository.UserDao;
import com.self.repository.entity.UserEntity;
import com.self.service.AuthService;
import com.self.service.UserService;
import com.self.beans.vo.User;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户相关操作
 * @author liuyong
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;
    @Resource
    private AuthService authService;

    @Override
    public User createUser(User user) {
        if (StringUtils.isBlank(user.getNickName())
                || StringUtils.isBlank(user.getPassWord())
                || StringUtils.isBlank(user.getPhoneNum())) {
            log.info("创建账号, 昵称: {} 密码: {}, 手机号: {} 为必填参数", user.getNickName(), user.getPassWord(), user.getPhoneNum());
            throw new BizException(ErrorDefinition.PARAM_MISS);
        }

        UserEntity userEntity = userDao.getByPhone(user.getPhoneNum());
        if (Objects.nonNull(userEntity)) {
            throw new BizException(ErrorDefinition.EXIST_PHONE);
        }

        UserEntity entity = buildEntity(user);
        int insertRes = userDao.insert(entity);
        if (insertRes > 0) {
            user.setId(entity.getId());
            return user;
        }
        throw new BizException(ErrorDefinition.INSERT_USER_FAIL);
    }

    @Override
    public User login(User user) {
        if (StringUtils.isBlank(user.getPhoneNum()) || StringUtils.isBlank(user.getPassWord())) {
            log.info("用户登录, 昵称: {} 密码: {} 为必填参数", user.getNickName(), user.getPassWord());
            throw new BizException(ErrorDefinition.PARAM_MISS);
        }

        UserEntity userEntity = userDao.getByPhone(user.getPhoneNum());
        if (Objects.isNull(userEntity)) {
            log.info("用户登录, 未找到用户记录通过手机号 {}", user.getPhoneNum());
            throw new BizException(ErrorDefinition.INVALID_PHONE);
        }

        boolean validPassWord = user.getPassWord().equals(userEntity.getPassWord());
        if (validPassWord) {
            user = buildVo(userEntity);
            user.setToken(authService.generateToken(user.getId()));
            return user;
        }
        throw new BizException(ErrorDefinition.INVALID_PASSWORD);
    }

    @Override
    public List<SearchedUser> search(long userId, String query) {
        List<UserEntity> userEntityList = userDao.serach(query);
        if (CollectionUtils.isEmpty(userEntityList)) {
            return Collections.emptyList();
        }

        return userEntityList.stream()
                .map(entity -> new SearchedUser(entity.getId(), entity.getNickName()))
                .collect(Collectors.toList());
    }

    private UserEntity buildEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setNickName(user.getNickName());
        entity.setPassWord(user.getPassWord());
        entity.setPhoneNum(user.getPhoneNum());

        return entity;
    }

    private User buildVo(UserEntity entity) {
        User user = new User();
        BeanUtils.copyProperties(entity, user);
        return user;
    }
}
