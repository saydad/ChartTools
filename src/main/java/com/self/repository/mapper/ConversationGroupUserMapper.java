package com.self.repository.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.self.repository.entity.ConversationGroupUserEntity;

/**
 * 会话用户关系操作
 * @author liuyong
 */
@Repository
public interface ConversationGroupUserMapper extends BaseMapper<ConversationGroupUserEntity> {

    /**
     * 通过指定的俩个用户，查询是否存在俩人的会话id
     * @param user1 用户1
     * @param user2 用户2
     * @return 俩人的会话关系信息
     */
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "conversationGroupId", column = "conversation_group_id"),
            @Result(property = "type", column = "type"),
            @Result(property = "userId", column = "user_id"),
    })
    @Select({
            "select a.id, a.conversation_group_id, a.type, a.user_id ",
            "from conversation_group_user as a join conversation_group_user b ",
            "on a.conversation_group_id = b.conversation_group_id ",
            "where a.user_id = #{user1} and b.user_id = #{user2} and a.type = 1"
    })
    ConversationGroupUserEntity getSingleChartRelation(@Param("user1") long user1, @Param("user2")long user2);
}
