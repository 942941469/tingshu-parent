package com.atguigu.tingshu.common.service;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public boolean send(String topic, String message) {
        CompletableFuture completableFuture = kafkaTemplate.send(topic, null, message);
        completableFuture.thenAccept(result -> {
            logger.debug("发送消息成功: topic={},value={}", topic, JSON.toJSONString(message));
        }).exceptionally(e -> {
            logger.error("发送消息失败: topic={},value={}", topic, JSON.toJSONString(message));
            return null;
        });
        return true;
    }
}
