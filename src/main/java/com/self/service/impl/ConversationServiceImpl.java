package com.self.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.self.beans.request.MessageSearchParam;
import com.self.beans.request.SearchSessionParam;
import com.self.beans.request.SendMessageParam;
import com.self.beans.vo.Message;
import com.self.beans.vo.SessionInfo;
import com.self.constant.ConversationType;
import com.self.repository.ConversationGroupDao;
import com.self.repository.ConversationGroupUserDao;
import com.self.repository.MessageDao;
import com.self.repository.UserDao;
import com.self.repository.UserSessionDao;
import com.self.repository.entity.ConversationGroupEntity;
import com.self.repository.entity.ConversationGroupUserEntity;
import com.self.repository.entity.MessageEntity;
import com.self.repository.entity.UserConversationTimeEntity;
import com.self.repository.entity.UserEntity;
import com.self.service.ConversationService;
import com.self.service.DistributedSessionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConversationServiceImpl implements ConversationService {

    private final UserDao userDao;
    private final MessageDao messageDao;
    private final UserSessionDao userSessionDao;
    private final ConversationGroupDao conversationGroupDao;
    private final ConversationGroupUserDao conversationGroupUserDao;

    private final DistributedSessionService distributedSessionService;

    @Autowired
    public ConversationServiceImpl(UserDao userDao, MessageDao messageDao,
            UserSessionDao userSessionDao, ConversationGroupDao conversationGroupDao,
            ConversationGroupUserDao conversationGroupUserDao, DistributedSessionService distributedSessionService) {
        this.userDao = userDao;
        this.messageDao = messageDao;
        this.userSessionDao = userSessionDao;
        this.conversationGroupDao = conversationGroupDao;
        this.conversationGroupUserDao = conversationGroupUserDao;
        this.distributedSessionService = distributedSessionService;
    }

    @Override
    public List<SessionInfo> sessionListByUser(long userId) {
        // ????????????????????????????????????
        List<ConversationGroupUserEntity> conversationGroupUserList = conversationGroupUserDao.batchGetByUserId(userId);
        if (CollectionUtils.isEmpty(conversationGroupUserList)) {
            return Collections.emptyList();
        }

        // ???????????????id - ??????????????????????????????????????????????????????
        Set<Long> singleChartConversationGroupIds = new HashSet<>();

        Set<Long> allConversationGroupIds = conversationGroupUserList.stream()
                .peek(item -> {
                    if (item.getType() == ConversationType.SINGLE_CHART.type) {
                        singleChartConversationGroupIds.add(item.getConversationGroupId());
                    }
                })
                .map(ConversationGroupUserEntity::getConversationGroupId).collect(Collectors.toSet());

        return acquireSessionInfos(allConversationGroupIds, singleChartConversationGroupIds, userId);
    }

    @Override
    public SessionInfo acquireSessionInfo(SearchSessionParam searchParam) {
        List<SessionInfo> sessionInfoList;
        if (searchParam.getType() == ConversationType.GROUP_CHART.type) {
            sessionInfoList =
                    acquireSessionInfos(Sets.newHashSet(searchParam.getTargetId()), Collections.emptySet(), searchParam.getCurUserId());
            return sessionInfoList.get(0);
        }

        //?????????????????????????????????id
        ConversationGroupUserEntity singleChartRelation =
                conversationGroupUserDao.getSingleChartRelation(searchParam.getCurUserId(), searchParam.getTargetId());

        //?????????????????????
        if (Objects.isNull(singleChartRelation)) {
            return createChart(ConversationType.SINGLE_CHART.type, "", Sets.newHashSet(searchParam.getCurUserId(), searchParam.getTargetId()));
        }

        Set<Long> conversationGroupIds = Sets.newHashSet(singleChartRelation.getConversationGroupId());
        sessionInfoList = acquireSessionInfos(conversationGroupIds, conversationGroupIds, searchParam.getCurUserId());
        return sessionInfoList.get(0);
    }

    /**
     * ??????????????????????????????
     * @param type ??????????????????
     * @param chartName ??????????????????
     * @param chartUsers ??????????????????
     * @return ??????????????????
     */
    @Override
    public SessionInfo createChart(int type, String chartName, Set<Long> chartUsers) {
        //????????????
        long now = System.currentTimeMillis();
        ConversationGroupEntity conversationGroupEntity = new ConversationGroupEntity(type, chartName, now);
        conversationGroupDao.insertEntity(conversationGroupEntity);
        //???????????????????????????
        List<ConversationGroupUserEntity> conversationGroupUserEntities = new ArrayList<>(chartUsers.size());
        for (Long chartUserId : chartUsers) {
            conversationGroupUserEntities.add(new ConversationGroupUserEntity(conversationGroupEntity.getId(), type, chartUserId));
        }
        conversationGroupUserDao.insertEntities(conversationGroupUserEntities);

        return new SessionInfo(conversationGroupEntity.getId(), chartName, "", now);
    }

    /**
     * ????????????session??????
     * @param allConversationGroupIds ?????????????????????id??????
     * @param singleChartConversationGroupIds ???????????????id
     * @param userId ???????????????
     * @return ??????session??????
     */
    private List<SessionInfo> acquireSessionInfos(Set<Long> allConversationGroupIds, Set<Long> singleChartConversationGroupIds, long userId) {
        //?????????????????????????????????
        Map<Long, Long> conversationIdAndUserIdMap = Collections.emptyMap();
        if (!CollectionUtils.isEmpty(singleChartConversationGroupIds)) {
            conversationIdAndUserIdMap = conversationGroupUserDao.batchGetUserByConversationGroupIds(singleChartConversationGroupIds).stream()
                    .filter(item -> item.getUserId() != userId)
                    .collect(Collectors.toMap(ConversationGroupUserEntity::getConversationGroupId, ConversationGroupUserEntity::getUserId));
        }

        //??????????????????
        HashSet<Long> singleChartTargetUserIds = new HashSet<>(conversationIdAndUserIdMap.values());
        List<UserEntity> userEntities = userDao.batchGetByIds(singleChartTargetUserIds);
        Map<Long, UserEntity> userIdMap = userEntities.stream()
                .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        //????????????????????????????????????????????????
        Map<Long, Long> conversationIdaAndTimestamp = userSessionDao.batchGetConversationSpecifyUser(userId, allConversationGroupIds)
                .stream().collect(Collectors.toMap(UserConversationTimeEntity::getConversationGroupId, UserConversationTimeEntity::getTimestamp));

        //????????????????????????
        List<ConversationGroupEntity> conversationGroupList = conversationGroupDao.batchGetByIds(allConversationGroupIds);

        //????????????????????????????????????????????????????????????
        Long lastTimestamp;
        List<Object> nextMesId;
        List<Object> previousMsgId;
        long now = System.currentTimeMillis();
        List<Object> msgIds = Lists.newArrayListWithCapacity(allConversationGroupIds.size());
        for (ConversationGroupEntity conversation : conversationGroupList) {
            lastTimestamp = conversationIdaAndTimestamp.get(conversation.getId());
            if (Objects.isNull(lastTimestamp)) {
                lastTimestamp = now;
            }

            nextMesId = messageDao.bachGetCurTimestampNextMsg(conversation.getId(), lastTimestamp);
            if (!CollectionUtils.isEmpty(nextMesId)) {
                msgIds.addAll(nextMesId);
            } else {
                //????????????????????????????????????????????????????????????
                previousMsgId = messageDao.batchGetCurTimestampPreviousMsg(conversation.getId(), lastTimestamp);
                if (!CollectionUtils.isEmpty(previousMsgId)) {
                    msgIds.addAll(previousMsgId);
                }
            }
        }

        Set<Long> msgIdLongs = msgIds.stream().map(item -> (Long) item).collect(Collectors.toSet());
        Map<Long, MessageEntity> conversationIdMsgMap = messageDao.batchGetByIds(msgIdLongs).stream()
                .collect(Collectors.toMap(MessageEntity::getConversationGroupId, Function.identity()));

        //????????????
        UserEntity userEntity;
        SessionInfo sessionInfo;
        MessageEntity messageEntity;
        List<SessionInfo> sessionInfoList = new ArrayList<>(allConversationGroupIds.size());
        for (ConversationGroupEntity conversation : conversationGroupList) {
            messageEntity = conversationIdMsgMap.get(conversation.getId());
            if (Objects.nonNull(messageEntity)) {
                sessionInfo = new SessionInfo(conversation.getId(), conversation.getName(), messageEntity.getMsg(), messageEntity.getTimestamp());
            } else {
                sessionInfo = new SessionInfo(conversation.getId(), conversation.getName(), "", System.currentTimeMillis());
            }
            if (conversation.getType() == ConversationType.SINGLE_CHART.type) {
                userEntity = userIdMap.get(conversationIdAndUserIdMap.get(conversation.getId()));
                sessionInfo.setName(userEntity.getNickName());
            }
            sessionInfoList.add(sessionInfo);
        }

        return sessionInfoList;
    }

    @Override
    public List<Message> messageList(MessageSearchParam param) {
        log.info("???????????? {}", JSON.toJSON(param));

        List<MessageEntity> messageList =
                messageDao.getMsgBySpecifySession(param.getConversationId(), param.getTimestamp(), param.isDown());
        if (CollectionUtils.isEmpty(messageList)) {
            return Collections.emptyList();
        }

        Set<Long> userIds = messageList.stream().map(MessageEntity::getUserId).collect(Collectors.toSet());
        Map<Long, String> userIdAndNickNameMap = userDao.batchGetByIds(userIds).stream().collect(Collectors.toMap(UserEntity::getId, UserEntity::getNickName));

        //????????????????????????????????????
        userSessionDao.updateOrSave(new UserConversationTimeEntity(param.getConversationId(), param.getCurUserId(), System.currentTimeMillis()));

        return messageList.stream().sorted(Comparator.comparing(MessageEntity::getTimestamp))
                .map(entity -> new Message(entity.getId(), userIdAndNickNameMap.get(entity.getUserId()),
                        entity.getMsg(), entity.getTimestamp(), entity.getUserId() == param.getCurUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean sendMsg(SendMessageParam param) {
        //????????????
        MessageEntity entity =
                new MessageEntity(param.getConversationId(), param.getUserId(), param.getMsg(), param.getTimestamp());
        int recordNum = messageDao.insertMsg(entity);
        if (recordNum <= 0) {
            log.info("???????????????????????? {}", JSON.toJSON(param));
            return false;
        }

        //???????????????????????????
        List<ConversationGroupUserEntity> userEntities = conversationGroupUserDao.batchGetUserByConversationGroupIds(Collections.singleton(param.getConversationId()));
        //????????????
        for (ConversationGroupUserEntity userEntity : userEntities) {
            distributedSessionService.notifyUser(param.getConversationId(), userEntity.getUserId());
        }
        return true;
    }
}
