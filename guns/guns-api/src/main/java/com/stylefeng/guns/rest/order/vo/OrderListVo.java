package com.stylefeng.guns.rest.order.vo;

import lombok.Data;

import java.util.List;

@Data
public class OrderListVo {

    private List<OrderVo> orderVoList;

    private long total;
}
