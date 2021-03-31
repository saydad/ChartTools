package com.self.service;

import java.util.List;

import com.self.beans.vo.SearchedUser;
import com.self.beans.vo.User;

public interface UserService {

    /**
     * 创建用户
     * @param user 用户数据
     * @return 用户信息
     */
    User createUser(User user);

    /**
     * 用户登录
     * @param user 用户数据
     * @return 用户信息
     */
    User login(User user);

    /**
     * 查询用户
     * @param userId 当前用户id
     * @param query 昵称/手机号
     * @return 用户列表
     */
    List<SearchedUser> search(long userId, String query);
}
