package com.stylefeng.guns.rest.order;

import com.stylefeng.guns.rest.order.vo.OrderVo;

public interface OrderService {

    Boolean isTrueSeats(String fieldId, String seatId);

    Boolean isSoldSeats(String fieldId, String seatId);

    OrderVo saveOrderInfo(String fieldId, String soldSeats, String seatsName, Integer userId);

    double getOrderPriceById(String OrderId);

    int getCinemaIdbyOrderId(String OrderId);

    String getSoldSeats(Integer fieldId);

}
