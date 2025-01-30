package com.atguigu.tingshu.account.consumer;

import com.atguigu.tingshu.account.service.UserAccountService;
import com.atguigu.tingshu.common.constant.KafkaConstant;
import com.atguigu.tingshu.model.account.UserAccount;
import io.micrometer.common.util.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @version 1.0
 */

@Component
public class AccountConsumer {

    @Autowired
    private UserAccountService userAccountService;

    /**
     * 监听Kafka中的用户注册消息
     *
     * @param record Kafka消息记录
     */
    @KafkaListener(topics = KafkaConstant.QUEUE_USER_REGISTER)
    public void initUserRegister(ConsumerRecord<String, String> record) {
        String userId = record.value();
        if (StringUtils.isNotEmpty(userId)) {
            UserAccount userAccount = new UserAccount();
            userAccount.setUserId(Long.parseLong(userId));
            userAccountService.save(userAccount);
        }
    }

}
