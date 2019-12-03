package com.stylefeng.guns.rest.Vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/11/28 22:38
 */
@Data
public class CinemaInfoVo implements Serializable {

    private static final long serialVersionUID = -8556736782793338891L;
    private String cinemaAdress;
    private Integer cinemaId;
    private String cinemaName;
    private String cinemaPhone;
    private String imgUrl;
}
