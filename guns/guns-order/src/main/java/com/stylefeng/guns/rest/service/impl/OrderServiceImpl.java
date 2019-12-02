package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stylefeng.guns.core.util.FileUtil;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeFieldTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeHallDictTMapper;
import com.stylefeng.guns.rest.order.OrderService;
import com.stylefeng.guns.rest.order.vo.OrderVo;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    MtimeFieldTMapper mtimeFieldTMapper;

    @Autowired
    MtimeHallDictTMapper mtimeHallDictTMapper;

    @Override
    public Boolean isTrueSeats(String fieldId, String seatId) {
        /*int hallId = mtimeFieldTMapper.selectHallIdById(fieldId);
        String seatAddress = mtimeHallDictTMapper.selectJsonById(hallId)*/;
        return null;
    }

    @Override
    public Boolean isSoldSeats(String fieldId, String seatId) {
        return null;
    }

    @Override
    public OrderVo saveOrderInfo(String fieldId, String soldSeats, String seatsName, Integer userId) {
        return null;
    }
}
