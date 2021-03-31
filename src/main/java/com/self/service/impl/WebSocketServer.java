package com.self.service.impl;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.self.beans.request.SendMessageParam;
import com.self.service.ConversationService;
import com.self.service.DistributedSessionService;
import com.self.util.SpringContextUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * webSocket会话,每个会话都有单独的WebSocketServer实例
 * @author liuyong
 */
@Slf4j
@Component
@ServerEndpoint("/api/imServer/{userId}")
public class WebSocketServer {

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     * */
    public static final Cache<String,WebSocketServer> WEB_SOCKET_MAP = CacheBuilder.newBuilder()
            .initialCapacity(100)
            .expireAfterWrite(Duration.ofMinutes(2))
            .build();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     * */
    private Session session;
    /**
     * 接收userId
     * */
    private String userId="";

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session,@PathParam("userId") String userId) {
        this.session = session;
        this.userId=userId;
        WebSocketServer oldWebSocketServer = WEB_SOCKET_MAP.getIfPresent(userId);
        if(Objects.nonNull(oldWebSocketServer)) {
            WEB_SOCKET_MAP.invalidate(userId);
            WEB_SOCKET_MAP.put(userId, this);
            //加入set中
        }else{
            WEB_SOCKET_MAP.put(userId,this);
        }

        try {
            log.info("{} 建立连接成功", userId);
            sendMessage("连接成功");
            //尝试清理该用户存在的旧会话对象
            SpringContextUtils.getBean(DistributedSessionService.class).clearUser(Long.parseLong(userId));
        } catch (IOException e) {
            log.error("用户:"+userId+",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法 -- 关闭/刷新浏览器时会触发
     */
    @OnClose
    public void onClose() {
        log.info("接收到关闭通知 {}", userId);
        WebSocketServer.clearUser(userId);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息: {} 报文 {}", userId, message);
        if (StringUtils.isBlank(message)) {
            return;
        }
        //心跳消息
        if (message.startsWith("hi")) {
            //session 续期
            WEB_SOCKET_MAP.put(userId, this);
            sendInfo("心跳回应", userId);
            return;
        }

        try {
            //解析发送的报文
            SendMessageParam sendMessage = JSON.parseObject(message, SendMessageParam.class);
            SpringContextUtils.getBean(ConversationService.class).sendMsg(sendMessage);
        }catch (Exception e){
            log.info("发现消息失败!!!", e);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.info("webSocket发生异常", error);
    }

    /**
     * 移除指定用户session
     * @param userId 用户id
     */
    public static void clearUser(String userId) {
        WebSocketServer oldWebSocketServer = WEB_SOCKET_MAP.getIfPresent(userId);
        if(Objects.nonNull(oldWebSocketServer)){
            WEB_SOCKET_MAP.invalidate(userId);
        }
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 发送自定义消息
     * */
    public static void sendInfo(String message, String userId) {
        try {
            if (StringUtils.isBlank(userId)) {
                log.error("用户" + userId + "为空！");
            }

            WebSocketServer webSocketServer = WEB_SOCKET_MAP.getIfPresent(userId);
            if (Objects.nonNull(webSocketServer)) {
                webSocketServer.sendMessage(message);
            } else {
                log.error("用户" + userId + ",不在线！");
            }
        } catch (Exception e) {
            log.error("推送消息异常 ", e);
        }
    }
}