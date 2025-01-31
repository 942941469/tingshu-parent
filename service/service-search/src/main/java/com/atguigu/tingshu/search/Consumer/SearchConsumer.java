package com.atguigu.tingshu.search.Consumer;

import com.atguigu.tingshu.common.constant.KafkaConstant;
import com.atguigu.tingshu.search.service.SearchService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @version 1.0
 */

@Component
@Slf4j
public class SearchConsumer {

    @Autowired
    private SearchService searchService;

    @KafkaListener(topics = KafkaConstant.QUEUE_ALBUM_UPPER)
    public void upperGoods(ConsumerRecord<String, String> consumerRecord) {
        String value = consumerRecord.value();
        if (StringUtils.isNotEmpty(value)) {
            searchService.upperAlbum(Long.valueOf(value));
        }
    }

    @KafkaListener(topics = KafkaConstant.QUEUE_ALBUM_LOWER)
    public void lowerGoods(ConsumerRecord<String, String> consumerRecord) {
        String value = consumerRecord.value();
        if (StringUtils.isNotEmpty(value)) {
            searchService.lowerAlbum(Long.valueOf(value));
        }
    }
}
