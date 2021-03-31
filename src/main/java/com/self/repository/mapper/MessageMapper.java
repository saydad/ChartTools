package com.self.repository.mapper;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.self.repository.entity.MessageEntity;

/**
 * 消息操作
 * @author liuyong
 */
@Repository
public interface MessageMapper extends BaseMapper<MessageEntity> {

}
