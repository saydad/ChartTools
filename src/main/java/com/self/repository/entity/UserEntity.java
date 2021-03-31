package com.self.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体
 * @author liuyong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class UserEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String nickName;
    /**
     * 加密
     */
    private String phoneNum;
    /**
     * 加密
     */
    private String passWord;

    private long createTime;

    /**
     * 最新在线时间戳
     */
    private long updateTime;
}
