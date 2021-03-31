package com.self.service.impl;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.self.beans.msg.SessionMsg;
import com.self.constant.MsgType;
import com.self.service.DistributedSessionService;

import lombok.extern.slf4j.Slf4j;

/**
 * 分布式session操作实现
 * @author liuyong
 */
@Slf4j
@Service
public class DistributedSessionServiceImpl implements DistributedSessionService {
    /**
     * kafka topic
     */
    private static final String CHART_TOPIC = "chart";

    /**
     * 当前机器标识
     */
    private String curMachine;
    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;

    /**
     * 消费者停止标记
     */
    private volatile boolean run = true;
    /**
     * 消费线程
     */
    private final ExecutorService consumerExecutorService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("kafka_consumer_%d").build());

    @PostConstruct
    public void init() {
        String machineFlag = System.getProperty("chart.machine");
        if (StringUtils.isBlank(machineFlag)) {
            throw new IllegalStateException("未设置标识每台机器唯一标识的属性 -Dchart.machine=xx");
        }
        curMachine = machineFlag;

        Properties producerProperties = new Properties();
        producerProperties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProperties.put(ProducerConfig.ACKS_CONFIG, "all");
        producerProperties.put(ProducerConfig.RETRIES_CONFIG, 1);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(producerProperties);

        Properties consumerProperties = new Properties();
        consumerProperties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //每台机器的group唯一,一样会消费不到消息
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "chart-" + curMachine);
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        consumerProperties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(consumerProperties);
        //启动消费线程
        consumerExecutorService.execute(this::consumerListener);
    }

    @PreDestroy
    public void destroy() {
        //消费者关闭
        run = false;
        //生成者关闭
        producer.close();
        //等待正在执行的任务结束后关闭线程池
        consumerExecutorService.shutdown();
    }

    @Override
    public void notifyUser(long conversationId, long userId) {
        SessionMsg msg = new SessionMsg(MsgType.SEND_MSG.type, userId, conversationId, curMachine);
        producer.send(new ProducerRecord<>(CHART_TOPIC, JSON.toJSONString(msg)));
    }

    @Override
    public void clearUser(long userId) {
        SessionMsg msg = new SessionMsg(MsgType.CLEAR_MSG.type, userId, -1, curMachine);
        producer.send(new ProducerRecord<>(CHART_TOPIC, JSON.toJSONString(msg)));
    }

    public void consumerListener() {
        consumer.subscribe(Collections.singleton(CHART_TOPIC));

        while (run) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
            if (records.isEmpty()) {
                continue;
            }

            SessionMsg sessionMsg;
            for (ConsumerRecord<String, String> record : records) {

                try {
                    sessionMsg = JSON.parseObject(record.value(), SessionMsg.class);
                } catch (Exception e) {
                    log.error("非法的消息格式 {}", record.value());
                    sessionMsg = null;
                }
                if (Objects.isNull(sessionMsg)) {
                    continue;
                }

                if (sessionMsg.getActionType() == MsgType.SEND_MSG.type) {
                    log.info("处理消息发送 {}", record.value());
                    //发送消息 -- 通知用户指定聊天会话有新消息
                    WebSocketServer.sendInfo(String.valueOf(sessionMsg.getConversationGroupId()), String.valueOf(sessionMsg.getTargetUserId()));
                } else if (sessionMsg.getActionType() == MsgType.CLEAR_MSG.type) {
                    log.info("处理连接清理 {}", record.value());
                    //清理用户
                    //自己发送的清理用户消息不处理
                    if (!curMachine.equals(sessionMsg.getMachine())) {
                        WebSocketServer.clearUser(String.valueOf(sessionMsg.getTargetUserId()));
                    }
                }
            }
        }

        consumer.close();
    }
}
