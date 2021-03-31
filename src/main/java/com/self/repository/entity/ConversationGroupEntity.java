package com.self.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会话实体
 * @author liuyong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("conversation_group")
public class ConversationGroupEntity {

    @TableId(type= IdType.AUTO)
    private Long id;

    /**
     * 会话类型 1-单聊 2-群聊
     */
    private Integer type;

    /**
     * 会话名称 - 单聊为''
     */
    private String name;

    private long createTime;

    public ConversationGroupEntity(int type, String name, long createTime) {
        this.type = type;
        this.name = name;
        this.createTime = createTime;
    }
}
