package com.stylefeng.guns.rest.mq;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoStockMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoStock;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/12/4 20:35
 */
@Component
@Slf4j
@ConfigurationProperties(prefix = "promo.consumer")
public class PromoConsumer {

    @Autowired
    MtimePromoStockMapper mtimePromoStockMapper;

    private DefaultMQPushConsumer consumer;

    private String namesrvAddr = "localhost:9876";

    private String topic = "promoStock";

    @PostConstruct
    public void init(){
        log.info("consumer正在初始化");
        consumer = new DefaultMQPushConsumer("promo_consumer");
        consumer.setNamesrvAddr(namesrvAddr);
        try {
            consumer.subscribe(topic, "*");
        } catch (MQClientException e) {
            e.printStackTrace();
            log.info("订阅失败");
        }
        log.info("准备注册listener");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                log.info("listener");
                MessageExt messageExt = msgs.get(0);
                byte[] body = messageExt.getBody();
                String stringBody = new String(body);
                HashMap map = JSON.parseObject(stringBody, HashMap.class);
                Integer promoId = (Integer)map.get("promoId");
                Integer amount = (Integer)map.get("amount");
                log.info("收到消息，promoId:{}， amount:{}",promoId,amount);
                //根据消息去数据减少库存
                MtimePromoStock mtimePromoStock = new MtimePromoStock();
                mtimePromoStock.setPromoId(promoId);
                MtimePromoStock stockInfo = mtimePromoStockMapper.selectOne(mtimePromoStock);
                stockInfo.setStock(stockInfo.getStock() - amount);
                EntityWrapper<MtimePromoStock> stockEntityWrapper = new EntityWrapper<>();
                stockEntityWrapper.eq("promo_id", promoId);
                Integer update = mtimePromoStockMapper.update(stockInfo, stockEntityWrapper);
                if (update < 1){
                    log.info("消费失败！扣减库存失败");
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
