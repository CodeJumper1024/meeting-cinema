package com.stylefeng.guns.rest.mq;

import com.alibaba.fastjson.JSON;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoStockMapper;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

@Component
public class Consumer {
    private DefaultMQPushConsumer mqPushConsumer;
    @Value("${mq.nameserver.addr}")
    private String addr;
    @Value("${mq.topic}")
    private String topic;
    @Autowired
    MtimePromoStockMapper mtimePromoStockMapper;
    @PostConstruct
    public void init() throws MQClientException {
        mqPushConsumer = new DefaultMQPushConsumer("consumer-group");
        mqPushConsumer.setNamesrvAddr(addr);
        mqPushConsumer.subscribe(topic,"*");
        mqPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt messageExt = list.get(0);
                byte[] body = messageExt.getBody();
                String bodyStr = new String(body);
                HashMap hashMap = JSON.parseObject(bodyStr, HashMap.class);
                Integer promoId = (Integer) hashMap.get("promoId");
                Integer stock = (Integer) hashMap.get("stock");
                // mapper
                Integer integer = mtimePromoStockMapper.decrease(stock,promoId);
                // 重试机制 16次

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        mqPushConsumer.start();
    }
}
