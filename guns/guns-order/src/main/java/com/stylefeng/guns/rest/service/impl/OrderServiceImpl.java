package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeFieldTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeHallDictTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.order.OrderService;
import com.stylefeng.guns.rest.order.vo.OrderListVo;
import com.stylefeng.guns.rest.order.vo.OrderVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    MtimeFieldTMapper mtimeFieldTMapper;

    @Autowired
    MtimeHallDictTMapper mtimeHallDictTMapper;

    @Autowired
    MoocOrderTMapper moocOrderTMapper;

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

    @Override
    public OrderListVo getOrderByUserId(Integer userId, Integer nowPage, Integer pageSize) {
        OrderListVo orderListVo = new OrderListVo();
        List<OrderVo> list = new ArrayList<>();

        Page page = new Page(nowPage, pageSize);
        EntityWrapper<MoocOrderT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("order_user",userId);
        List<MoocOrderT> moocOrderTS = moocOrderTMapper.selectPage(page, entityWrapper);
        if(CollectionUtils.isEmpty(moocOrderTS)){
            return orderListVo;
        }
        Integer count = moocOrderTMapper.selectCount(entityWrapper);
        for(MoocOrderT moocOrderT : moocOrderTS){
            OrderVo orderVo = new OrderVo();
            BeanUtils.copyProperties(moocOrderT,orderVo);
            list.add(orderVo);
        }
        orderListVo.setTotal(count);
        orderListVo.setOrderVoList(list);
        return orderListVo;
    }


}
