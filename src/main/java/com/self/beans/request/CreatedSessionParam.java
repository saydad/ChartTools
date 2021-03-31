package com.self.beans.request;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建session参数
 * @author liuyong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatedSessionParam {
    /**
     * 聊天会话类型
     */
    private int type;
    /**
     * 聊天会话名称
     */
    private String chartName;
    /**
     * 参与的用户
     */
    private Set<Long> chartUsers;
    /**
     * 创建的用户
     */
    private Long createUser;
}
