package com.self.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会话成员关系实体
 * @author liuyong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("conversation_group_user")
public class ConversationGroupUserEntity {

    @TableId(type= IdType.AUTO)
    private Long id;
    /**
     * 会话id
     */
    private Long conversationGroupId;
    /**
     * 会话类型 1-单聊 2-群聊
     */
    private Integer type;
    /**
     * 成员id
     */
    private Long userId;

    public ConversationGroupUserEntity(long conversationGroupId, int type, long userId) {
        this.conversationGroupId = conversationGroupId;
        this.type = type;
        this.userId = userId;
    }
}
