package com.stylefeng.guns.rest.Vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/12/3 21:21
 */
@Data
public class PromoInfoVo implements Serializable {
    String cinemaAddress;
    Integer cinemaId;
    String cinemaName;
    String description;
    String endTime;
    String imgAddress;
    Integer price;
    String startTime;
    Integer status;
    Integer stock;
    Integer uuid;
}
