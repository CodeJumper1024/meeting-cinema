package com.stylefeng.guns.rest.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderListVo implements Serializable {

    private static final long serialVersionUID = -4453809287608100148L;

    private List<OrderVo> orderVoList;

    private long total;
}
