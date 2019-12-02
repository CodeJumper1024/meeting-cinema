package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.order.OrderService;
import com.stylefeng.guns.rest.order.vo.OrderVo;
import com.stylefeng.guns.rest.utils.ConnectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@Service(interfaceClass = OrderService.class, timeout = 5000)
public class OrderServiceImpl implements OrderService {

    @Autowired
    MtimeFieldTMapper mtimeFieldTMapper;

    @Autowired
    MtimeHallDictTMapper mtimeHallDictTMapper;

    @Autowired
    MoocOrderTMapper orderTMapper;

    @Autowired
    MtimeCinemaTMapper mtimeCinemaTMapper;

    @Autowired
    MtimeFilmTMapper mtimeFilmTMapper;

    @Override
    public Boolean isTrueSeats(String fieldId, String seatId) {
        int hallId = mtimeFieldTMapper.selectHallIdById(fieldId);
        String uri = "http://localhost:1818/" + mtimeHallDictTMapper.selectJsonById(hallId);
        String input = ConnectionUtils.readFileToString(uri);
        JSONObject jsonObject = JSONObject.parseObject(input);
        String idsString = jsonObject.getString("ids");
        String[] ids = idsString.split(",");
        Integer id = Integer.valueOf(seatId);
        if (id <= ids.length) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean isSoldSeats(String fieldId, String seatId) {
        List<String> seatsIdsStr = orderTMapper.selectOrderSeatsIdsByFieldId(fieldId);
        for (String seatsIdStr : seatsIdsStr) {
            String[] strings = seatsIdStr.split(",");
            for (String string : strings) {
                if (seatId.equals(string)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public OrderVo saveOrderInfo(String fieldId, String soldSeats, String seatsName, Integer userId) {

        int cinemaId = mtimeFieldTMapper.selectCinemaIdById(fieldId);
        String cinemaName = mtimeCinemaTMapper.selectCinemaNameById(cinemaId);

        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String fieldTime = dateFormat.format(date);

        int filmId = mtimeFieldTMapper.selectFilmIdById(fieldId);
        String filmName = mtimeFilmTMapper.selectFilmNameById(filmId);

        String uuid = String.valueOf(UUID.randomUUID());

        String[] split = soldSeats.split(",");
        Double price = mtimeFieldTMapper.selectPriceById(fieldId);
        String orderPrice = String.valueOf(split.length * price);

        String orderStatus = "未支付";

        String orderTimestamp = String.valueOf(new Timestamp(System.currentTimeMillis()));

        int result = orderTMapper.insertOrder(uuid, cinemaId, fieldId, filmId, soldSeats, seatsName, price, orderPrice, userId);

        OrderVo orderVo = new OrderVo();
        orderVo.setCinemaName(cinemaName);
        orderVo.setFieldTime(fieldTime);
        orderVo.setFilmName(filmName);
        orderVo.setOrderId(uuid);
        orderVo.setOrderStatus(orderStatus);
        orderVo.setOrderTimestamp(orderTimestamp);
        orderVo.setOrderPrice(orderPrice);
        orderVo.setSeatsName(seatsName);
        return orderVo;
    }

    @Override
    public double getOrderPriceById(String OrderId) {
        double orderPrice = orderTMapper.getOrderPriceById(OrderId);
        return orderPrice;
    }

    @Override
    public int getCinemaIdbyOrderId(String OrderId) {
        int cinemaId = orderTMapper.getCinemaIdbyOrderId(OrderId);
        return cinemaId;
    }

    public String getSoldSeats(Integer fieldId) {
        String fieldId_s = Integer.toHexString(fieldId);
        List<String> seatsIdsStr = orderTMapper.selectOrderSeatsIdsByFieldId(fieldId_s);
        StringBuffer stringBuffer = new StringBuffer();
        for (String s : seatsIdsStr) {
            stringBuffer.append(s).append(",");
        }
        return stringBuffer.toString();
    }

}
