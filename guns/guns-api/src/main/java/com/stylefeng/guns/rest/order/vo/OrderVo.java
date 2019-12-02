package com.stylefeng.guns.rest.order.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderVo implements Serializable {

    private static final long serialVersionUID = -8296987274259941723L;

    String cinemaName;

    String fieldTime;

    String filmName;

    String orderId;

    String orderPrice;

    String orderStatus;

    String orderTimestamp;

    String seatsName;
}
