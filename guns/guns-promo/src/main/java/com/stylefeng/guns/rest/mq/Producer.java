package com.stylefeng.guns.rest.mq;

import com.alibaba.fastjson.JSON;
import com.stylefeng.guns.core.constant.StockLogStatus;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeStockLogMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeStockLog;
import com.stylefeng.guns.rest.promo.PromoService;
import com.stylefeng.guns.rest.promo.vo.PromoOrderVo;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;

@Component
public class Producer {
    private DefaultMQProducer producer;
    private TransactionMQProducer transactionMQProducer;
    @Value("${mq.nameserver.addr}")
    private String addr;
    @Value("${mq.topic}")
    private String topic;
    @Value("${mq.transactionproducergroup}")
    private String transactiongroup;
    @Autowired
    private PromoService promoService;
    @Autowired
    private MtimeStockLogMapper stockLogMapper;
    @PostConstruct
    public void init(){
        producer = new DefaultMQProducer("producer_group");
        producer.setNamesrvAddr(addr);
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        transactionMQProducer = new TransactionMQProducer(transactiongroup);
        transactionMQProducer.setNamesrvAddr(addr);
        try {
            transactionMQProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        // 设置一个事务监听回调器
        transactionMQProducer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object args) {
                HashMap hashMap = (HashMap) args;
                Integer promoId = (Integer) hashMap.get("promoId");
                Integer amount = (Integer) hashMap.get("amount");
                Integer userId = (Integer) hashMap.get("userId");
                String stockLogId = (String) hashMap.get("stockLogId");
                PromoOrderVo promoOrderVo = null;

                try {

                    //执行本地事务  插入订单 扣减redis中的库存
                    promoOrderVo = promoService.savePromoOrderVo(promoId,userId,amount,stockLogId);

                    //执行完成之后库存流水状态被更改

                } catch (Exception e) {
                    e.printStackTrace();
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                if (promoOrderVo == null) {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }

                return LocalTransactionState.COMMIT_MESSAGE;
            }

            //回查本地事务状态
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {

                byte[] body = msg.getBody();
                String bodyStr = new String(body);
                HashMap hashMap = JSON.parseObject(bodyStr, HashMap.class);

                String stockLogId = (String) hashMap.get("stockLogId");
                MtimeStockLog stockLog = stockLogMapper.selectById(stockLogId);

                Integer status = stockLog.getStatus();
                //如果status 是成功 表示本地事务执行成功
                if (status == StockLogStatus.SUCCESS.getIndex()){
                    return LocalTransactionState.COMMIT_MESSAGE;
                }
                //如果status是失败，表示本地事务执行失败
                if (status == StockLogStatus.FAIL.getIndex()) {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }

                return LocalTransactionState.UNKNOW;
            }
        });
    }
    public Boolean decreaseStock(Integer promoId,Integer stock){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("promoId",promoId);
        hashMap.put("stock",stock);
        Message message = new Message(topic, JSON.toJSONString(hashMap).getBytes(Charset.forName("utf-8")));
        SendResult sendResult = null;
        try {
            sendResult = producer.send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (sendResult == null) {
            return false;
        } else {
            SendStatus sendStatus = sendResult.getSendStatus();
            if (SendStatus.SEND_OK.equals(sendStatus)){
                return true;
            }
            return false;
        }
    }
    // 发送事务型消息
    public Boolean sendStockMessageIntransaction(Integer promoId, Integer amount, Integer userId, String stockLogId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("promoId",promoId);
        hashMap.put("amount",amount);
        hashMap.put("userId",userId);
        hashMap.put("stockLogId",stockLogId);

        HashMap<String , Object> argsMap = new HashMap<>();
        argsMap.put("promoId",promoId);
        argsMap.put("amount",amount);
        argsMap.put("userId",userId);
        argsMap.put("stockLogId",stockLogId);

        String jsonString = JSON.toJSONString(hashMap);
        Message message = new Message(topic, jsonString.getBytes(Charset.forName("utf-8")));

        TransactionSendResult transactionSendResult = null;
        try {
            transactionSendResult= transactionMQProducer.sendMessageInTransaction(message, argsMap);
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        }
        if(transactionSendResult == null){
            return  false;
        }
        //本地事务执行状态
        LocalTransactionState localTransactionState = transactionSendResult.getLocalTransactionState();
        if (LocalTransactionState.COMMIT_MESSAGE.equals(localTransactionState)) {
            return true;
        }else{
            return false;
        }
    }
}
