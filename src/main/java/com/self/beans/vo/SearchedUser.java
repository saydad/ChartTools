package com.self.beans.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.self.constant.ConversationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchedUser {
    private long id;
    /**
     * 用户昵称
     */
    @JsonProperty("value")
    private String nickName;

    /**
     * 会话类型
     */
    private Integer type = ConversationType.SINGLE_CHART.type;

    public SearchedUser(long id, String nickName) {
        this.id = id;
        this.nickName = nickName;
    }
}
