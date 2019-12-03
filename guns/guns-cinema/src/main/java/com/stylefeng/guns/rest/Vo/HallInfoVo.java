package com.stylefeng.guns.rest.Vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/11/29 17:59
 */
@Data
public class HallInfoVo implements Serializable {
    private static final long serialVersionUID = -4549428786612421382L;
    private String discountPrice;
    private Integer hallFieldId;
    private String hallName;
    private String soldSeats;
    private String seatFile;
    private Integer price;
}
