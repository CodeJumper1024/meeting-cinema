package com.stylefeng.guns.rest.cinema.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CinemaVO implements Serializable {

    private static final long serialVersionUID = 5604806473371096976L;

    private String cinemaAddress;

    private String cinemaName;

    private Integer minimumPrice;

    private Integer uuid;

    private String totalPage;

}
