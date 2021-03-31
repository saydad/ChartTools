package com.self.beans.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户
 * @author liuyong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private long id;
    /**
     * 用户昵称
     */
    @JsonProperty("nickname")
    private String nickName;
    /**
     * 手机号
     */
    @JsonProperty("phone")
    private String phoneNum;
    /**
     * 密码
     */
    @JsonProperty("password")
    private String passWord;

    /**
     * 用户token
     */
    private String token;
}
