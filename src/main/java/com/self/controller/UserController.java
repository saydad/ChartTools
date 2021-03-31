package com.self.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.self.beans.vo.SearchedUser;
import com.self.service.UserService;
import com.self.util.ResultUtil;
import com.self.beans.vo.Result;
import com.self.beans.vo.User;

/**
 * 用户操作
 * @author liuyong
 */
@CrossOrigin
@RestController
@RequestMapping(path = "/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping(path = "/register")
    public Result<User> createUser(@RequestBody User user) {
        return ResultUtil.toResult(() -> userService.createUser(user));
    }

    @PostMapping(path = "/login")
    public Result<User> login(@RequestBody User user) {
        return ResultUtil.toResult(() -> userService.login(user));
    }

    @GetMapping(path = "/search")
    public Result<List<SearchedUser>> searchUser(@RequestParam("userId") long userId,
            @RequestParam("query") String query) {
        return ResultUtil.toResult(() -> userService.search(userId, query));
    }
}
