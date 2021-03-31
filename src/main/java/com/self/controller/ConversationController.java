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

import com.self.beans.request.CreatedSessionParam;
import com.self.beans.request.MessageSearchParam;
import com.self.beans.request.SearchSessionParam;
import com.self.beans.vo.Message;
import com.self.beans.vo.Result;
import com.self.beans.vo.SessionInfo;
import com.self.service.ConversationService;
import com.self.util.ResultUtil;

/**
 * 会话相关操作
 * @author liuyong
 */
@CrossOrigin
@RestController
@RequestMapping(path = "/api/conversation")
public class ConversationController {

    @Resource
    private ConversationService conversationService;

    @GetMapping(path = "/list/specifyUser")
    public Result<List<SessionInfo>> getSessionList(@RequestParam long userId) {
        return ResultUtil.toResult(() -> conversationService.sessionListByUser(userId));
    }

    @PostMapping(path = "/specifyChart")
    public Result<SessionInfo> acquireSessionInfo(@RequestBody SearchSessionParam searchParam) {
        return ResultUtil.toResult(() -> conversationService.acquireSessionInfo(searchParam));
    }

    @PostMapping(path = "/createChart")
    public Result<SessionInfo> createSessionInfo(@RequestBody CreatedSessionParam param) {
        param.getChartUsers().add(param.getCreateUser());
        return ResultUtil.toResult(() -> conversationService.createChart(param.getType(), param.getChartName(), param.getChartUsers()));
    }

    @PostMapping(path = "/message/list")
    public Result<List<Message>> messageList(@RequestBody MessageSearchParam param) {
        return ResultUtil.toResult(() -> conversationService.messageList(param));
    }
}
