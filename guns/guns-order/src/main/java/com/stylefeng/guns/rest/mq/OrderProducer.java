package com.stylefeng.guns.rest.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/12/6 15:23
 */
@Component
@Slf4j
public class OrderProducer {
    private DefaultMQProducer producer;

    private String namesrvAddr = "localhost:9876";

    private String topic = "order";

    @PostConstruct
    public void init(){
        producer = new DefaultMQProducer("order_producer");
        producer.setNamesrvAddr(namesrvAddr);
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        log.info("producer初始化成功");
    }

    public Boolean cancleUnpaidOrder(String orderId){
        Message message = new Message(topic, JSON.toJSONString(orderId).getBytes(Charset.forName("utf-8")));
        message.setDelayTimeLevel(7);
        SendResult sendResult = null;
        try {
            sendResult = producer.send(message);
            log.info("发送消息完成");
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (sendResult == null){
            return false;
        }
        if (SendStatus.SEND_OK.equals(sendResult)){
            return true;
        }else {
            return false;
        }
    }
}
