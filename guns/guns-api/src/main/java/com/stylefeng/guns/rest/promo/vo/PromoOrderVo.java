package com.stylefeng.guns.rest.promo.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PromoOrderVo implements Serializable {
    private static final long serialVersionUID = 7178915897754898438L;
    private String uuid;
    private Integer userId;
    private Integer cinemaId;
    private String exchangeCode;
    private Date StartTime;
    private Date EndTime;
    private Integer amount;
    private Integer price;
    private Date createTime;
}
