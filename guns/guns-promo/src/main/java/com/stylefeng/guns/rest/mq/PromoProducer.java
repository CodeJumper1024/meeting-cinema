package com.stylefeng.guns.rest.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeStockLogMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeStockLog;
import com.stylefeng.guns.rest.promo.PromoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/12/4 20:35
 */
@Component
@Slf4j
@ConfigurationProperties(prefix = "promo.producer")
public class PromoProducer {

    @Autowired
    PromoService promoService;

    @Autowired
    MtimeStockLogMapper mtimeStockLogMapper;

    private DefaultMQProducer producer;

    private TransactionMQProducer transactionMQProducer;

    private String namesrvAddr = "localhost:9876";

    private String topic = "promoStock";

    @PostConstruct
    public void init(){
        producer = new DefaultMQProducer("promo_producer");
        producer.setNamesrvAddr(namesrvAddr);
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        log.info("producer初始化成功");

        transactionMQProducer = new TransactionMQProducer("promo_transactionProducer");
        transactionMQProducer.setNamesrvAddr(namesrvAddr);
        try {
            transactionMQProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }

        //设置事务监听回调器
        transactionMQProducer.setTransactionListener(new TransactionListener() {
            //执行本地事务
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object args) {
                byte[] body = message.getBody();
                String stringBody = new String(body);
                HashMap maps = JSON.parseObject(stringBody, HashMap.class);
                Boolean localTransactionResult = promoService.executeLocalTransaction(maps);
                if (localTransactionResult){
                    return LocalTransactionState.COMMIT_MESSAGE;
                }else {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
            }

            //回查本地事务状态
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                byte[] body = messageExt.getBody();
                String bodyStr = new String(body);
                HashMap msgMap = JSON.parseObject(bodyStr, HashMap.class);
                String stockLogId = (String) msgMap.get("stockLogId");
                MtimeStockLog mtimeStockLog = mtimeStockLogMapper.selectById(stockLogId);
                Integer status = mtimeStockLog.getStatus();
                if(status == 2){
                    return LocalTransactionState.COMMIT_MESSAGE;
                }else if (status == 3){
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }else {
                    return LocalTransactionState.UNKNOW;
                }
            }
        });

        log.info("trsactionProducer初始化成功");
    }


    public Boolean reduceStock(Integer promoId, Integer amount){
        HashMap<String, Integer> map = new HashMap<>();
        map.put("promoId", promoId);
        map.put("amount", amount);
        Message message = new Message(topic, JSON.toJSONString(map).getBytes(Charset.forName("utf-8")));
        SendResult sendResult = null;
        try {
            sendResult = producer.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("发送消息完成");
        if (sendResult == null){
            return false;
        }else {
            SendStatus sendStatus = sendResult.getSendStatus();
            if (SendStatus.SEND_OK.equals(sendStatus)){
                return true;
            }else {
                return false;
            }
        }
    }

    public Boolean createPromoOrderInMqTransaction(Integer userId, Integer promoId, Integer amount, String stockLogId) {
        //构造消息
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", userId);
        hashMap.put("promoId", promoId);
        hashMap.put("amount", amount);
        hashMap.put("stockLogId", stockLogId);

        HashMap<String, Object> argsMap = new HashMap<>();
        hashMap.put("userId", userId);
        hashMap.put("promoId", promoId);
        hashMap.put("amount", amount);
        hashMap.put("stockLogId", stockLogId);

        String jsonStr = JSON.toJSONString(hashMap);
        byte[] messageBody = jsonStr.getBytes(Charset.forName("utf-8"));
        Message message = new Message(topic, messageBody);

        //发送消息, transactionProducer发布的消息,接收方会以事务状态维持, 等待提交或回滚
        TransactionSendResult transactionSendResult = null;
        try {
            transactionSendResult = transactionMQProducer.sendMessageInTransaction(message, argsMap);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("发送事务消息-stock异常");
            return false;
        }
        if (transactionSendResult == null){
            return false;
        }

        //发送消息成功开始执行本地事务
        LocalTransactionState localTransactionState = transactionSendResult.getLocalTransactionState();
        if (LocalTransactionState.COMMIT_MESSAGE.equals(localTransactionState)) {
            return true;
        }else{
            return false;
        }
    }
}
