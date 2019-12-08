package com.stylefeng.guns.rest.mq;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/12/6 15:24
 */
@Component
@Slf4j
public class OrderConsumer {

    @Autowired
    MoocOrderTMapper moocOrderTMapper;

    private DefaultMQPushConsumer consumer;

    private String namesrvAddr = "localhost:9876";

    private String topic = "order";

    @PostConstruct
    public void init(){
        log.info("consumer开始初始化");
        consumer = new DefaultMQPushConsumer("order_consumer");
        consumer.setNamesrvAddr(namesrvAddr);
        try {
            consumer.subscribe(topic, "*");
        } catch (MQClientException e) {
            e.printStackTrace();
            log.info("订阅失败");
        }
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                log.info("listener开始初始化");
                MessageExt messageExt = msgs.get(0);
                byte[] body = messageExt.getBody();
                String stringBody = new String(body);
                String orderId = JSON.parseObject(stringBody, String.class);
                log.info("收到消息，orderId:{}",orderId);
                //根据消息去删除指定订单
                EntityWrapper<MoocOrderT> orderTEntityWrapper = new EntityWrapper<>();
                if (orderId == null){
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                orderTEntityWrapper.eq("UUID", orderId);
                Integer num = moocOrderTMapper.delete(orderTEntityWrapper);
                if (num < 1){
                    log.info("删除失效订单失败");
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        try {
            consumer.start();
        } catch (MQClientException e) {
            log.info("consumer启动失败");
            e.printStackTrace();
        }
    }
}
