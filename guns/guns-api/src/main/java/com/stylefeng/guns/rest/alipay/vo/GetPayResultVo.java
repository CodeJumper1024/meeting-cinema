package com.stylefeng.guns.rest.alipay.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class GetPayResultVo implements Serializable {
    private static final long serialVersionUID = -3990901412527733135L;
    String orderId;
    String orderMsg;
    Integer orderStatus;
}
